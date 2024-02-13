package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Playlist;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5Playlists extends StandardStatisticsCommand {
    private static final int MAX_RESULTS = 5;
    private ArrayList<Playlist> allUsersPlaylists;

    // Singleton instance field
    private static GetTop5Playlists instance = null;

    /**
     * Constructs a new GetTop5Playlists with the specified parameters.
     *
     * @param node               The JSON node associated with the command.
     * @param timestamp          The timestamp of the command.
     * @param allUsersPlaylists  The list of all playlists from all users.
     */

    // make constructor private for singleton implementation
    private GetTop5Playlists(final String command, final ObjectNode node, final Integer timestamp,
                            final ArrayList<Playlist> allUsersPlaylists) {
        super(command, timestamp, node);
        this.allUsersPlaylists = allUsersPlaylists;
    }

    /**
     * Gets the singleton instance of GetTop5Playlists.
     *
     * @param node               The JSON node associated with the command.
     * @param timestamp          The timestamp of the command.
     * @param allUsersPlaylists  The list of all playlists from all users.
     * @return The singleton instance of GetTop5Playlists.
     */
    public static GetTop5Playlists getInstance(final String command, final ObjectNode node,
                                               final Integer timestamp,
                                               final ArrayList<Playlist> allUsersPlaylists) {
        if (instance == null) {
            instance = new GetTop5Playlists(command, node, timestamp, allUsersPlaylists);
        } else {
            instance.setCommand(command);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
            instance.setAllUsersPlaylists(allUsersPlaylists);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the top 5 playlists based on followers.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());
        node.putPOJO("result", this.getTop5PlaylistsNames());
        return node;
    }


    /**
     * Retrieves all playlists sorted in descending order of followers.
     *
     * @return An ArrayList of playlists sorted by the number of followers in descending order.
     */
    public ArrayList<Playlist> getAllPlaylistsInDescendingOrder() {
        // sort allUsersPlaylists by each playlist's number of followers in descending order
        allUsersPlaylists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(final Playlist playlist, final Playlist t1) {
                return t1.getFollowers() - playlist.getFollowers();
            }
        });

        return allUsersPlaylists;
    }

    /**
     * Retrieves the names of the top 5 playlists sorted in descending order of followers.
     *
     * @return An ArrayList of the names of the top 5 playlists sorted by the number of
     * followers in descending order.
     */
    public ArrayList<String> getTop5PlaylistsNames() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Playlist> listOfAllUsersPlaylists = getAllPlaylistsInDescendingOrder();

        if (listOfAllUsersPlaylists.size() < MAX_RESULTS) {
            for (int i = 0; i < listOfAllUsersPlaylists.size(); i++) {
                result.add(listOfAllUsersPlaylists.get(i).getName());
            }
            return result;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            result.add(listOfAllUsersPlaylists.get(i).getName());
        }
        return result;
    }



    /**
     * Gets the list of all playlists from all users.
     *
     * @return The list of all playlists from all users.
     */
    public ArrayList<Playlist> getAllUsersPlaylists() {
        return allUsersPlaylists;
    }

    /**
     * Sets the list of all playlists from all users.
     *
     * @param allUsersPlaylists The list of all playlists to set.
     */
    public void setAllUsersPlaylists(final ArrayList<Playlist> allUsersPlaylists) {
        this.allUsersPlaylists = allUsersPlaylists;
    }

    /**
     * Gets the singleton instance of GetTop5Playlists.
     *
     * @return The singleton instance of GetTop5Playlists.
     */
    public static GetTop5Playlists getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of GetTop5Playlists.
     *
     * @param instance The singleton instance to set.
     */
    public static void setInstance(final GetTop5Playlists instance) {
        GetTop5Playlists.instance = instance;
    }
}
