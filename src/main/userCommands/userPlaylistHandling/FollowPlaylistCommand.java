package main.userCommands.userPlaylistHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class FollowPlaylistCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_PLAYLIST = 3;

    // Singleton instance field
    private static FollowPlaylistCommand instance = null;

    /**
     * Constructs a new FollowPlaylistCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */

    // make constructor private for singleton implementation
    private FollowPlaylistCommand(final String command, final String username,
                                  final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of FollowPlaylistCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of FollowPlaylistCommand.
     */
    public static FollowPlaylistCommand getInstance(final String command, final String username,
                                                    final Integer timestamp,
                                                    final ObjectNode node) {
        if (instance == null) {
            instance = new FollowPlaylistCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the follow or unfollow operation, which includes the user, timestamp,
     * and a message indicating the success or failure of the operation.
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

                // check if the user has selected anything
                if (!user.getSelected()) {
                    node.put("message", "Please select a source before following or unfollowing.");
                    return node;
                }

                // check if the selected audio entity is a playlist
                if (user.getLastSearchTypeIndicator() != TYPE_PLAYLIST) {
                    node.put("message", "The selected source is not a playlist.");
                    return node;
                }

                // check if the user does not own the playlist
                if (user.getUsername().equals(user.getLastSelectedPlaylist().getOwner())) {
                    node.put("message", "You cannot follow or unfollow your own playlist.");
                    return node;
                }

                // check if the visibility of the playlist is public
                if (user.getLastSelectedPlaylist().getVisibility()) {
                    // cannot follow private playlist
                    return node;
                }

                // check if the user's following playlists list is empty
                if (user.getFollowing().isEmpty()) {
                    user.getFollowing().add(user.getLastSelectedPlaylist());
                    Integer followers = user.getLastSelectedPlaylist().getFollowers();
                    user.getLastSelectedPlaylist().setFollowers(followers + 1);
                    node.put("message", "Playlist followed successfully.");
                    return node;
                }

                // check if the user has already added this playlist to his following list
                for (int i = 0; i < user.getFollowing().size(); i++) {
                    String playlistName = user.getLastSelectedPlaylist().getName();
                    if (user.getFollowing().get(i).getName().equals(playlistName)) {
                        // the user is already following the selected playlist => unfollow
                        user.getFollowing().remove(i);
                        Integer followers = user.getLastSelectedPlaylist().getFollowers();
                        user.getLastSelectedPlaylist().setFollowers(followers - 1);
                        node.put("message", "Playlist unfollowed successfully.");
                        return node;
                    }
                }
                /* if we reached this point, it means that the user isn't folowing
                the selected playlist => add it to the follow list */
                user.getFollowing().add(user.getLastSelectedPlaylist());
                Integer followers = user.getLastSelectedPlaylist().getFollowers();
                user.getLastSelectedPlaylist().setFollowers(followers + 1);
                node.put("message", "Playlist followed successfully.");
            }
        }
        return node;
    }

}
