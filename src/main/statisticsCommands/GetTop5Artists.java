package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.Artist;

import java.util.ArrayList;
import java.util.Comparator;

public final class GetTop5Artists extends StandardStatisticsCommand {
    private static final int MAX_RESULTS = 5;

    // Singleton instance field
    private static GetTop5Artists instance = null;

    private GetTop5Artists(final String command, final ObjectNode node, final Integer timestamp) {
        super(command, timestamp, node);
    }

    /**
     * Gets the singleton instance of `GetTop5Artists` with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param node      the JSON node associated with the command.
     * @param timestamp the timestamp of the command.
     * @return the singleton instance of `GetTop5Artists`.
     */
    public static GetTop5Artists getInstance(final String command, final ObjectNode node,
                                             final Integer timestamp) {
        if (instance == null) {
            instance = new GetTop5Artists(command, node, timestamp);
        } else {
            instance.setCommand(command);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the top 5 artists based on followers.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());
        node.putPOJO("result", this.getTopt5ArtistsNames());
        return node;
    }

    /**
     * Retrieves all artists in descending order by likes.
     *
     * @return a list of all artists in descending order by likes.
     */
    public ArrayList<Artist> getAllArtistsInDescendingOrder() {
        /* copy all the artists from the main list to a new list and sort
        it in descending order by the artists' number of likes */
        ArrayList<Artist> allArtists = new ArrayList<>();
        for (Artist artist : Main.artistsList) {
            artist.updateArtistNumberOfLikes();
            allArtists.add(artist);
        }

        allArtists.sort(new Comparator<Artist>() {
            @Override
            public int compare(final Artist artist1, final Artist artist2) {
                return artist2.getNumberOfLikes() - artist1.getNumberOfLikes();
            }
        });

        return allArtists;
    }

    /**
     * Retrieves the names of the top 5 artists.
     *
     * @return a list of names of the top 5 artists.
     */
    public ArrayList<String> getTopt5ArtistsNames() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Artist> allArtists = getAllArtistsInDescendingOrder();

        if (allArtists.size() < MAX_RESULTS) {
            for (int i = 0; i < allArtists.size(); i++) {
                result.add(allArtists.get(i).getUsername());
            }
            return result;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            result.add(allArtists.get(i).getUsername());
        }
        return result;
    }
}
