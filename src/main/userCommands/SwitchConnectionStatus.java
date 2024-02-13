package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.NormalUser;

public final class SwitchConnectionStatus extends StandardCommandForUserPlayer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;
    private static SwitchConnectionStatus instance = null;

    private SwitchConnectionStatus(final String command, final String username,
                                   final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * @param command  the command to be executed
     * @param username the username of the user that executes the command
     * @param timestamp the timestamp when the command was issued
     * @param node     the node that contains the information needed for the command
     * @return an instance of the SwitchConnectionStatus command or
     * an already existing instance (if the command was called before)
     */
    public static SwitchConnectionStatus getInstance(final String command, final String username,
                                                     final Integer timestamp,
                                                     final ObjectNode node) {
        if (instance == null) {
            instance = new SwitchConnectionStatus(command, username, timestamp, node);
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
     * and the updated connection status of the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getCommandMessage());
        return node;
    }

    /**
     * Changes the connection status of the user.
     * <p>
     * This method changes the connection status of the user
     * and returns a message indicating the result.
     * </p>
     *
     * @param user the user that executes the command
     * @return A string indicating the result of the status change.
     */
    private String changeStatus(final NormalUser user) {
        if (user.getConnectionStatus()) {
            user.setConnectionStatus(false);
        } else {
            user.setConnectionStatus(true);
        }
        return this.username + " has changed status successfully.";
    }

    /**
     * Executes the switch of connection status for the user.
     * <p>
     * This method checks the current loaded status of the user and updates
     * the connection status accordingly.
     * If a song, podcast, playlist, or album is loaded, it handles the
     * switch between online and offline status,
     * taking into account whether the media is playing or paused. If
     * nothing is loaded, it simply switches the connection status.
     * </p>
     *
     * @return A string indicating the result of the status change
     * or an error message if the user is not found.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                // if we reached this point, it means that the user is a normal user
                // check if there was anything playing when switching the connection status
                if (!user.getLoaded()) {
                    // nothing even loaded => just switch status
                    return changeStatus(user);
                }

                if (user.getLastLoadTypeIndicator() == TYPE_SONG) {
                    // a song is loaded in the player
                    if (!user.getLastLoadedSongPlayInfo().getSongPaused()) {
                        // the song is currently playing
                        if (user.getConnectionStatus()) {
                            /* we are switching from online to offline while
                            the song was playing => update status */
                            if (!user.isSongStillLoaded(this.timestamp)) {
                                return changeStatus(user);
                            }
                        } else {
                            /* we are switching from offline to online while
                            the song was playing => just update the timestamp */
                            user.getLastLoadedSongPlayInfo().setLastPlayTimestamp(this.timestamp);
                        }
                    }
                    return changeStatus(user);
                }

                if (user.getLastLoadTypeIndicator() == TYPE_PODCAST) {
                    // a podcast is loaded in the player
                    if (!user.getLastLoadedPodcast().getPodcastPaused()) {
                        // the podcast is currently playing
                        if (user.getConnectionStatus()) {
                            /* we are switching from online to offline while
                            the podcast was playing => update status */
                            if (!user.isPodcastStillLoaded(this.timestamp)) {
                                return changeStatus(user);
                            }
                        } else {
                            /* we are switching from offline to online while the
                            podcast was playing => just update the timestamp */
                            user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
                        }
                    }
                    return changeStatus(user);
                }

                if (user.getLastLoadTypeIndicator() == TYPE_PLAYLIST) {
                    // a playlist is currently playing
                    if (!user.getLastLoadedPlaylist().getPaused()) {
                        //the playlist is currently playing
                        if (user.getConnectionStatus()) {
                            /* we are switching from online to offline while the
                             playlist was playing => update status */
                            if (user.isPlaylistStillLoaded(this.timestamp)) {
                                return changeStatus(user);
                            }
                        } else {
                            /* we are switching from offline to online while the
                            playlist was playing => just update the timestamp */
                            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                        }
                    }
                    return changeStatus(user);
                }

                if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
                    // an album is currently playing
                    if (!user.getLastLoadedAlbum().getPaused()) {
                        //the album is currently playing
                        if (user.getConnectionStatus()) {
                            /* we are switching from online to offline while
                            the album was playing => update status */
                            if (user.isAlbumStillLoaded(this.timestamp)) {
                                return changeStatus(user);
                            }
                        } else {
                            /* we are switching from offline to online while
                            the album was playing => just update the timestamp */
                            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                        }
                    }
                    return changeStatus(user);
                }

            }
        }

        /* if we reached this point, it means that we haven't
        found the user in the normal users' list */
        return checkUsernameExistence();
    }



}
