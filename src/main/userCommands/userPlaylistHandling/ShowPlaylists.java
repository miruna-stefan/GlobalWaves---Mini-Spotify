package main.userCommands.userPlaylistHandling;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Playlist;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class ShowPlaylists extends StandardCommandForUserPlayer {

    // Singleton instance field
    private static ShowPlaylists instance = null;

    /**
     * Constructs a new ShowPlaylists command with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */

    // make constructor private for singleton implementation
    private ShowPlaylists(final String command, final String username, final Integer timestamp,
                          final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of ShowPlaylists.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of ShowPlaylists.
     */
    public static ShowPlaylists getInstance(final String command, final String username,
                                            final Integer timestamp,
                                            final ObjectNode node) {
        if (instance == null) {
            instance = new ShowPlaylists(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, including the user's playlists, their names,
     * songs, visibility and followers.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        ArrayList<ObjectNode> result = new ArrayList<>();
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                // iterate through the user's list of playlists
                for (Playlist playlist : user.getPlaylists()) {

                    ObjectNode playlistNode = JsonNodeFactory.instance.objectNode();
                    playlistNode.put("name", playlist.getName());
                    playlistNode.putPOJO("songs", playlist.getSongsNames());

                    if (playlist.getVisibility()) {
                        playlistNode.put("visibility", "private");
                    } else {
                        playlistNode.put("visibility", "public");
                    }
                    playlistNode.put("followers", playlist.getFollowers());
                    result.add(playlistNode);
                }
                node.putPOJO("result", result);
            }
        }
        return node;
    }

}
