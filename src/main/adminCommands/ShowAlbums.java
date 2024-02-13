package main.adminCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;

import java.util.ArrayList;

public final class ShowAlbums extends StandardAdminCommand {
    // Singleton instance field
    private static ShowAlbums instance = null;
    private ShowAlbums(final String command, final String username,
                       final Integer timestamp, final ObjectNode node) {
        super(command, username, timestamp, node);
    }

    /**
     * Gets the singleton instance of ShowAlbums.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The ShowAlbums instance.
     */
    public static ShowAlbums getInstance(final String command, final String username,
                                         final Integer timestamp,
                                         final ObjectNode node) {
        if (instance == null) {
            instance = new ShowAlbums(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        ArrayList<ObjectNode> result = new ArrayList<>();
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                for (Album album : artist.getAlbums()) {
                    ObjectNode albumNode = JsonNodeFactory.instance.objectNode();
                    albumNode.put("name", album.getName());
                    albumNode.putPOJO("songs", album.getSongsNames());
                    result.add(albumNode);
                }
                node.putPOJO("result", result);
            }
        }

        return node;
    }
}
