package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import users.NormalUser;

import java.util.ArrayList;

public final class ShowPreferredSongs extends StandardCommandForUserPlayer {

    // Singleton instance field
    private static ShowPreferredSongs instance = null;

    /**
     * Constructs a new ShowPreferredSongs command with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    private ShowPreferredSongs(final String command, final String username, final Integer timestamp,
                              final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of ShowPreferredSongs.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of ShowPreferredSongs.
     */
    public static ShowPreferredSongs getInstance(final String command, final String username,
                                                 final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new ShowPreferredSongs(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, including the names of the songs preferred by the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        ArrayList<String> likedSongsNames = new ArrayList<>();
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                for (SongPlayInfo songPlayInfo : user.getLikedSongs()) {
                    likedSongsNames.add(songPlayInfo.getSong().getName());
                }
            }
        }
        node.putPOJO("result", likedSongsNames);
        return node;
    }


}
