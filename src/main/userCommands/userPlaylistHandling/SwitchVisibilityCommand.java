package main.userCommands.userPlaylistHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class SwitchVisibilityCommand extends StandardCommandForUserPlayer {
    private Integer playlistId;

    // Singleton instance field
    private static SwitchVisibilityCommand instance = null;

    /**
     * Constructs a new SwitchVisibilityCommand with the specified parameters.
     *
     * @param username   the username associated with the command.
     * @param timestamp  the timestamp of the command.
     * @param playlistId the ID of the playlist to switch visibility.
     * @param node       the JSON node associated with the command.
     */

    // make constructor private for singleton implementation
    private SwitchVisibilityCommand(final String command, final String username,
                                    final Integer timestamp,
                                    final Integer playlistId, final ObjectNode node) {
        super(command, timestamp, node, username);
        this.playlistId = playlistId;
    }

    /**
     * Gets the singleton instance of SwitchVisibilityCommand.
     *
     * @param username   the username associated with the command.
     * @param timestamp  the timestamp of the command.
     * @param playlistId the ID of the playlist to switch visibility.
     * @param node       the JSON node associated with the command.
     * @return the singleton instance of SwitchVisibilityCommand.
     */
    public static SwitchVisibilityCommand getInstance(final String command, final String username,
                                                      final Integer timestamp,
                                                      final Integer playlistId,
                                                      final ObjectNode node) {
        if (instance == null) {
            instance = new SwitchVisibilityCommand(command, username, timestamp, playlistId, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setPlaylistId(playlistId);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated visibility status of the specified playlist.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                // check if the user is offline
                if (!user.getConnectionStatus()) {
                    // the user is offline
                    node.put("message", this.getUsername() + " is offline.");
                    return node;
                }

                /* check if the playlistID is within the bounds of
                the current user's list of playlists */
                if (user.getPlaylists().size() < this.getPlaylistId()) {
                    node.put("message", "The specified playlist ID is too high.");
                    return node;
                }
                if (user.getPlaylists().get(this
                        .getPlaylistId() - 1).getVisibility()) {
                    user.getPlaylists().get(this
                            .getPlaylistId() - 1).setVisibility(false);
                    node.put("message", "Visibility status updated successfully to public.");
                } else {
                    user.getPlaylists().get(this
                            .getPlaylistId() - 1).setVisibility(true);
                    node.put("message", "Visibility status updated successfully to private.");
                }
            }
        }
        return node;
    }

    /**
     * Gets the ID of the playlist.
     *
     * @return The ID of the playlist.
     */
    public Integer getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets the ID of the playlist.
     *
     * @param playlistId The new ID of the playlist.
     */
    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }


}
