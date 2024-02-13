package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class LikeCommand extends StandardCommandForUserPlayer {

    private static final int TYPE_SONG = 1;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static LikeCommand instance = null;

    /**
     * Constructs a new LikeCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */

    // make constructor private for singleton design pattern
    private LikeCommand(final String command, final String username,
                        final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of LikeCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of LikeCommand.
     */
    public static LikeCommand getInstance(final String command, final String username,
                                          final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new LikeCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the like command.
     *
     * @return The ObjectNode containing information about the like operation.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            return node;
        }
        node.put("message", this.getCommandMessage());
        return node;
    }


    /**
     * Likes or unlikes a given song based on its current like status for the user.
     *
     * <p>If the song is already liked, it unlikes it; otherwise, it likes the song.
     *
     * @param user       The user who is registering the like or unlike.
     * @param loadedSong The song to be liked or unliked.
     * @return A message indicating the success of the like or unlike operation.
     */
    public String likeSong(final NormalUser user, final SongPlayInfo loadedSong) {
        // check if the song has already been liked
        if (!user.getLikedSongs().isEmpty()) {
            for (SongPlayInfo songPlayInfo : user.getLikedSongs()) {
                if (songPlayInfo.getSong().getName().equals(loadedSong.getSong().getName())) {
                    // the song has already been liked, so we need to unlike it
                    user.getLikedSongs().remove(songPlayInfo);
                    songPlayInfo.setNumberOfLikes(songPlayInfo.getNumberOfLikes() - 1);
                    return "Unlike registered successfully.";
                }
            }
        }

        // if we have reached this point, it means that the loaded song hasn't been liked
        user.getLikedSongs().add(loadedSong);
        SongPlayInfo songPlayInfo = user.getLikedSongs().get(user.getLikedSongs().size() - 1);
        songPlayInfo.setNumberOfLikes(songPlayInfo.getNumberOfLikes() + 1);
        return "Like registered successfully.";
    }

    /**
     * Executes the like or unlike command on the currently loaded song.
     *
     * <p>The method checks if the song is currently playing, updates the song status,
     * and performs the like or unlike operation based on the song's current like status.
     *
     * @param user The user for whom the like or unlike command is executed.
     * @return A message indicating the success of the like or unlike operation.
     */
    public String executeLikeOnASong(final NormalUser user) {
        return likeSong(user, user.getLastLoadedSongPlayInfo());
    }

    /**
     * Executes the like or unlike command on the currently loaded song in a playlist.
     *
     * <p>The method identifies the current song in the playlist, checks if the playlist is playing,
     * updates the playlist status, and performs the like or unlike operation based on the song's
     * current like status.
     *
     * @param user The user for whom the like or unlike command is executed.
     * @return A message indicating the success of the like or unlike operation.
     */
    public String executeLikeOnASongInAPlaylist(final NormalUser user) {
        Integer currentSongIndex = user.getLastLoadedPlaylist().getCurrentSongIndex();
        Playlist playlist = user.getLastLoadedPlaylist();
        if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
            ArrayList<SongPlayInfo> playlistSongs = playlist.getPlaylistSongs();
            return likeSong(user, playlistSongs.get(currentSongIndex));
        } else {
            ArrayList<SongPlayInfo> shuffledPlaylistSongs = playlist.getShuffledPlaylistSongs();
            return likeSong(user, shuffledPlaylistSongs.get(currentSongIndex));
        }
    }

    /**
     * Executes the like or unlike command on the currently loaded song in an album.
     *
     * <p>The method identifies the current song in the album, checks if the album is playing,
     * updates the album status, and performs the like or unlike operation based on the song's
     * current like status.
     *
     * @param user The user for whom the like or unlike command is executed.
     * @return A message indicating the success of the like or unlike operation.
     */
    public String executeLikeOnASongInAnAlbum(final NormalUser user) {
        Integer currentSongIndex = user.getLastLoadedAlbum().getCurrentSongIndex();
        Album album = user.getLastLoadedAlbum();
        if (!user.getLastLoadedAlbum().getShuffleStatus()) {
            ArrayList<SongPlayInfo> albumSongs = album.getSongs();
            return likeSong(user, albumSongs.get(currentSongIndex));
        } else {
            ArrayList<SongPlayInfo> shuffledAlbumSongs = album.getShuffledSongs();
            return likeSong(user, shuffledAlbumSongs.get(currentSongIndex));
        }
    }

    /**
     * Executes the like command based on the user's last loaded source.
     *
     * <p>The method checks if the user had previously loaded anything, if the loaded source is an
     * individual song or a song in a playlist, and then executes the appropriate like or unlike
     * command.
     *
     * @return A message indicating the success of the like or unlike operation.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                // check if the user had previously loaded anything
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before liking or unliking.";
                }

                /* check if the loaded source is a song or a
                song in a playlist or a song in an album */
                if (user.getLastLoadTypeIndicator() != TYPE_SONG
                        && user.getLastLoadTypeIndicator() != TYPE_PLAYLIST
                        && user.getLastLoadTypeIndicator() != TYPE_ALBUM) {
                    return "Loaded source is not a song.";
                }

                // treat the case of an individual song
                if (user.getLastLoadTypeIndicator() == TYPE_SONG) {
                    return executeLikeOnASong(user);
                }

                if (user.getLastLoadTypeIndicator() == TYPE_PLAYLIST) {
                    return executeLikeOnASongInAPlaylist(user);
                }
                if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
                    return executeLikeOnASongInAnAlbum(user);
                }
            }
        }
        return "Like registered successfully.";
    }


}
