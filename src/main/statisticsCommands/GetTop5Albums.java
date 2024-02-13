package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class GetTop5Albums extends StandardStatisticsCommand {
    private static final int MAX_RESULTS = 5;

    // Singleton instance field
    private static GetTop5Albums instance = null;

    private GetTop5Albums(final String command, final ObjectNode node, final Integer timestamp) {
        super(command, timestamp, node);
    }

    /**
     * Gets the singleton instance of `GetOnlineUsers` with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param node      the JSON node associated with the command.
     * @param timestamp the timestamp of the command.
     * @return the singleton instance of `GetOnlineUsers`.
     */
    public static GetTop5Albums getInstance(final String command, final ObjectNode node,
                                            final Integer timestamp) {
        if (instance == null) {
            instance = new GetTop5Albums(command, node, timestamp);
        } else {
            instance.setCommand(command);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());
        node.putPOJO("result", this.getTop5AlbumsNames());
        return node;
    }

    /**
     * Retrieves all albums from all artists in descending order by likes.
     *
     * @return a list of all albums in descending order by likes.
     */
    public ArrayList<Album> getAllAlbumsInDescendingOrder() {
        // create a list with all albums from all artists
        ArrayList<Album> allAlbums = new ArrayList<>();
        for (Artist artist : Main.artistsList) {
            for (Album album : artist.getAlbums()) {
                // update the album's number of likes
                album.updateAlbumNumberOfLikes();
                allAlbums.add(album);
            }
        }

        //sort the albums in descending order by likes
        allAlbums.sort(new Comparator<Album>() {
            @Override
            public int compare(final Album album1, final Album album2) {
                // if the number of likes is equal, sort in lexicographical order
                if (Objects.equals(album1.getNumberOfLikes(), album2.getNumberOfLikes())) {
                    return album1.getName().compareTo(album2.getName());
                }
                return album2.getNumberOfLikes() - album1.getNumberOfLikes();
            }
        });

        return allAlbums;
    }

    /**
     * Retrieves the names of the top 5 albums.
     *
     * @return a list of names of the top 5 albums.
     */
    public ArrayList<String> getTop5AlbumsNames() {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Album> allAlbums = getAllAlbumsInDescendingOrder();

        if (allAlbums.size() < MAX_RESULTS) {
            for (int i = 0; i < allAlbums.size(); i++) {
                result.add(allAlbums.get(i).getName());
            }
            return result;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            result.add(allAlbums.get(i).getName());
        }
        return result;
    }

}
