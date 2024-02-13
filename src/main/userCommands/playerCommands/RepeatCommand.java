package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class RepeatCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static RepeatCommand instance = null;

    /**
     * Constructs a new RepeatCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private for singleton pattern
    private RepeatCommand(final String command, final String username, final Integer timestamp,
                          final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of RepeatCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of RepeatCommand.
     */
    public static RepeatCommand getInstance(final String command, final String username,
                                            final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new RepeatCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the repeat mode change.
     *
     * @return The ObjectNode containing information about the repeat mode change.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                if (!user.getConnectionStatus()) {
                    // the user is offline
                    node.put("message", this.getUsername() + " is offline.");
                    return node;
                }
                if (!user.stillHasSomethingLoaded(this.getTimestamp())) {
                    // check if any source has been loaded
                    node.put("message", "Please load a source before setting the repeat status.");
                    return node;
                }
                if (user.getLastSearchTypeIndicator() == TYPE_SONG) {
                    node.put("message", this.changeSongRepeatMode(user));
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                    node.put("message", this.changePodcastRepeatMode(user));
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    node.put("message", this.changePlaylistRepeatMode(user));
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    node.put("message", this.changeAlbumRepeatMode(user));
                }
            }
        }
        return node;
    }


    /**
     * Changes the repeat mode for a song.
     *
     * @param user The user for whom the repeat mode is changed.
     * @return A message indicating the result of the repeat mode change for the song.
     */
    public String changeSongRepeatMode(final NormalUser user) {
        if (user.getLastLoadedSongPlayInfo().getRepeatStatus() == 0) {
            user.getLastLoadedSongPlayInfo().setRepeatStatus(1);
            return "Repeat mode changed to repeat once.";
        }
        if (user.getLastLoadedSongPlayInfo().getRepeatStatus() == 1) {
            user.getLastLoadedSongPlayInfo().setRepeatStatus(2);
            return "Repeat mode changed to repeat infinite.";
        }
        if (user.getLastLoadedSongPlayInfo().getRepeatStatus() == 2) {
            user.getLastLoadedSongPlayInfo().setRepeatStatus(0);
            return "Repeat mode changed to no repeat.";
        }
        return null;
    }

    /**
     * Changes the repeat mode for a podcast.
     *
     * @param user The user for whom the repeat mode is changed.
     * @return A message indicating the result of the repeat mode change for the podcast.
     */
    public String changePodcastRepeatMode(final NormalUser user) {

        if (user.getLastLoadedPodcast().getRepeatStatus() == 0) {
            user.getLastLoadedPodcast().setRepeatStatus(1);
            return "Repeat mode changed to Repeat Once.";
        }

        if (user.getLastLoadedPlaylist().getRepeatStatus() == 1) {
            user.getLastLoadedPodcast().setRepeatStatus(2);
            return "Repeat mode changed to Repeat Infinite.";
        }

        if (user.getLastLoadedPlaylist().getRepeatStatus() == 2) {
            user.getLastLoadedPodcast().setRepeatStatus(0);
            return "Repeat mode changed to No Repeat.";
        }
        return null;
    }


    /**
     * Changes the repeat mode for a playlist.
     *
     * @param user The user for whom the repeat mode is changed.
     * @return A message indicating the result of the repeat mode change for the playlist.
     */
    public String changePlaylistRepeatMode(final NormalUser user) {
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 0) {
            user.getLastLoadedPlaylist().setRepeatStatus(1);
            return "Repeat mode changed to repeat all.";
        }

        if (user.getLastLoadedPlaylist().getRepeatStatus() == 1) {
            user.getLastLoadedPlaylist().setRepeatStatus(2);
            return "Repeat mode changed to repeat current song.";
        }

        if (user.getLastLoadedPlaylist().getRepeatStatus() == 2) {
            user.getLastLoadedPlaylist().setRepeatStatus(0);
            return "Repeat mode changed to no repeat.";
        }
        return null;
    }

    /**
     * Changes the repeat mode for an album.
     *
     * @param user The user for whom the repeat mode is changed.
     * @return A message indicating the result of the repeat mode change for the album.
     */
    public String changeAlbumRepeatMode(final NormalUser user) {
        if (user.getLastLoadedAlbum().getRepeatStatus() == 0) {
            user.getLastLoadedAlbum().setRepeatStatus(1);
            return "Repeat mode changed to repeat all.";
        }

        if (user.getLastLoadedAlbum().getRepeatStatus() == 1) {
            user.getLastLoadedAlbum().setRepeatStatus(2);
            return "Repeat mode changed to repeat current song.";
        }

        if (user.getLastLoadedAlbum().getRepeatStatus() == 2) {
            user.getLastLoadedAlbum().setRepeatStatus(0);
            return "Repeat mode changed to no repeat.";
        }
        return null;
    }


}
