package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class PrevCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static PrevCommand instance = null;

    /**
     * Constructs a new PrevCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private for singleton implementation
    private PrevCommand(final String command, final String username, final Integer timestamp,
                        final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of PrevCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of PrevCommand.
     */
    public static PrevCommand getInstance(final String command, final String username,
                                          final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new PrevCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the previous track command.
     *
     * @return The ObjectNode containing information about the previous track operation.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            return node;
        }
        node.put("message", this.getMessage());
        return node;
    }


    /**
     * Executes the Prev command to return to the previous track in a playlist.
     *
     * <p>The method checks if a playlist is loaded, if it's paused, and then delegates
     * to the appropriate method based on the playlist's shuffle status.
     *
     * @param user The user for whom the previous track command is executed.
     * @return A message indicating the success of returning to the previous track in the playlist.
     */
    public String executePlaylistPrev(final NormalUser user) {
        Playlist playlist = user.getLastLoadedPlaylist();
        if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
            return updateCurrentToPrevPlaylist(user, playlist.getPlaylistSongs());
        } else {
            return updateCurrentToPrevPlaylist(user, playlist.getShuffledPlaylistSongs());
        }

    }

    /**
     * Executes the Prev command to return to the previous track in an album.
     *
     * <p>The method checks if an album is loaded, if it's paused, and then delegates
     * to the appropriate method based on the album's shuffle status.
     *
     * @param user The user for whom the previous track command is executed.
     * @return A message indicating the success of returning to the previous track in the album.
     */
    public String executeAlbumPrev(final NormalUser user) {
        Album album = user.getLastLoadedAlbum();
        if (!user.getLastLoadedAlbum().getShuffleStatus()) {
            return updateCurrentToPrevAlbum(user, album.getSongs());
        } else {
            return updateCurrentToPrevAlbum(user, album.getShuffledSongs());
        }

    }

    /**
     * Updates the current song to the previous one in a playlist.
     *
     * <p>The method considers the current song's position and the repeat status for
     * playlist playback.
     *
     * @param user     The user for whom the previous song in the playlist is updated.
     * @param songList The list of songs in the playlist.
     * @return A message indicating the success of returning to the previous song in the playlist.
     */
    public String updateCurrentToPrevPlaylist(final NormalUser user,
                                              final ArrayList<SongPlayInfo> songList) {
        Playlist playlist = user.getLastLoadedPlaylist();
        Integer currentSongSecond = playlist.getCurrentSongSecond();
        Integer currentSongIndex = playlist.getCurrentSongIndex();
        if (currentSongSecond > 0) {
            // go to the beginning of the same song
            user.getLastLoadedPlaylist().setCurrentSongSecond(0);
            user.getLastLoadedPlaylist().setPaused(false);
            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
            String currentTrackName = songList.get(currentSongIndex).getSong().getName();
            return "Returned to previous track successfully. The current track is "
                    + currentTrackName + ".";
        } else {
            //check if this is the first song of the playlist
            if (currentSongIndex != 0) {
                user.getLastLoadedPlaylist().setCurrentSongSecond(0);
                user.getLastLoadedPlaylist().setCurrentSongIndex(currentSongIndex - 1);
                user.getLastLoadedPlaylist().setPaused(false);
                user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                String currentTrackName = songList.get(currentSongIndex - 1).getSong().getName();
                return "Returned to previous track successfully. The current track is "
                        + currentTrackName + ".";
            }

            // replay playlist from the beginning
            user.getLastLoadedPlaylist().setCurrentSongSecond(0);
            user.getLastLoadedPlaylist().setCurrentSongIndex(0);
            user.getLastLoadedPlaylist().setPaused(false);
            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
            String currentTrackName = songList.get(0).getSong().getName();
            return "Returned to previous track successfully. The current track is "
                    + currentTrackName + ".";
        }
    }


    /**
     * Updates the current song to the previous one in an album.
     *
     * <p>The method considers the current song's position and the repeat status for
     * album playback.
     *
     * @param user     The user for whom the previous song in the album is updated.
     * @param songList The list of songs in the album.
     * @return A message indicating the success of returning to the previous song in the album.
     */
    public String updateCurrentToPrevAlbum(final NormalUser user,
                                           final ArrayList<SongPlayInfo> songList) {
        Album album = user.getLastLoadedAlbum();
        Integer currentSongSecond = album.getCurrentSongSecond();
        Integer currentSongIndex = album.getCurrentSongIndex();
        if (currentSongSecond > 0) {
            // go to the beginning of the same song
            user.getLastLoadedAlbum().setCurrentSongSecond(0);
            user.getLastLoadedAlbum().setPaused(false);
            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
            String currentTrackName = songList.get(currentSongIndex).getSong().getName();
            return "Returned to previous track successfully. The current track is "
                    + currentTrackName + ".";
        } else {
            //check if this is the first song of the album
            if (currentSongIndex != 0) {
                user.getLastLoadedAlbum().setCurrentSongSecond(0);
                user.getLastLoadedAlbum().setCurrentSongIndex(currentSongIndex - 1);
                user.getLastLoadedAlbum().setPaused(false);
                user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                String currentTrackName = songList.get(currentSongIndex - 1).getSong().getName();
                return "Returned to previous track successfully. The current track is "
                        + currentTrackName + ".";
            }

            // replay album from the beginning
            user.getLastLoadedAlbum().setCurrentSongSecond(0);
            user.getLastLoadedAlbum().setCurrentSongIndex(0);
            user.getLastLoadedAlbum().setPaused(false);
            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
            String currentTrackName = songList.get(0).getSong().getName();
            return "Returned to previous track successfully. The current track is "
                    + currentTrackName + ".";
        }
    }

    /**
     * Retrieves the message based on the user's last loaded source and executes
     * the corresponding previous command.
     *
     * <p>The method checks if the user had previously loaded anything and then delegates to the
     * appropriate handler for the playlist.
     *
     * @return A message indicating the success of returning to the previous track.
     */
    public String getMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before returning to the previous track.";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    return executePlaylistPrev(user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    return executeAlbumPrev(user);
                }
            }
        }
        return null;
    }


}
