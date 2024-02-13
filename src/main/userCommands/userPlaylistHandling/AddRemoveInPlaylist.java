package main.userCommands.userPlaylistHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class AddRemoveInPlaylist extends StandardCommandForUserPlayer {
    private Integer playlistId;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static AddRemoveInPlaylist instance = null;

    /**
     * Constructs a new AddRemoveInPlaylist with the specified parameters.
     *
     * @param username    the username associated with the command.
     * @param timestamp   the timestamp of the command.
     * @param playlistId  the ID of the playlist to which the song will be added or removed.
     * @param node        the JSON node associated with the command.
     */
    private AddRemoveInPlaylist(final String command, final String username,
                                final Integer timestamp, final Integer playlistId,
                                final ObjectNode node) {
        super(command, timestamp, node, username);
        this.playlistId = playlistId;
    }

    /**
     * Gets the singleton instance of AddRemoveInPlaylist.
     *
     * @param username    the username associated with the command.
     * @param timestamp   the timestamp of the command.
     * @param playlistId  the ID of the playlist to which the song will be added or removed.
     * @param node        the JSON node associated with the command.
     * @return the singleton instance of AddRemoveInPlaylist.
     */
    public static AddRemoveInPlaylist getInstance(final String command, final String username,
                                                  final Integer timestamp, final Integer playlistId,
                                                  final ObjectNode node) {
        if (instance == null) {
            instance = new AddRemoveInPlaylist(command, username, timestamp, playlistId, node);
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
     * Prints the result of the add or remove song command in the playlist.
     *
     * @return The ObjectNode containing information about the command execution.
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
     * Gets the ID of the playlist associated with the command.
     *
     * @return The ID of the playlist.
     */
    public Integer getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets the ID of the playlist associated with the command.
     *
     * @param playlistId The ID of the playlist to set.
     */
    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Executes the command add or remove a song from a playlist.
     *
     * <p>The method checks if a user has previously loaded a song, if the loaded source is a song,
     * if the specified playlist exists, and then adds or removes the song accordingly.
     *
     * @return A message indicating the success of adding or removing the song from the playlist.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                // check if the user had previously loaded anything
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before adding to or removing from the playlist.";
                }

                // check if the entity that was loaded is a song
                if (user.getLastLoadTypeIndicator() != 1
                        && user.getLastLoadTypeIndicator() != TYPE_ALBUM) {
                    return "The loaded source is not a song.";
                }

                // check if the specified playlist exists in the list
                if (user.getPlaylists().size() < this.playlistId) {
                    return "The specified playlist does not exist.";
                }

                SongPlayInfo loadedSong;
                if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
                    //store the song that is currently playing in the album
                    Album album = user.getLastLoadedAlbum();
                    if (!album.getShuffleStatus()) {
                        loadedSong = album.getSongs().get(album.getCurrentSongIndex());
                    } else {
                        loadedSong = album.getShuffledSongs().get(album.getCurrentSongIndex());
                    }
                } else {
                    loadedSong = user.getLastLoadedSongPlayInfo();
                }

                // search for the currently loaded song in the playlist
                Playlist currentPlaylist = user.getPlaylists().get(this.playlistId - 1);

                for (SongPlayInfo songPlayInfo : currentPlaylist.getPlaylistSongs()) {
                    if (songPlayInfo != null) {
                        if (songPlayInfo.getSong().getName()
                                .equals(loadedSong.getSong().getName())) {
                            // this song already exists in the playlist, so remove it
                            currentPlaylist.getPlaylistSongs().remove(songPlayInfo);
                            return "Successfully removed from playlist.";
                        }
                    }
                }

                /* if we reached this point, it means that we have not found the
                currently loaded song in the playlist */
                currentPlaylist.getPlaylistSongs().add(loadedSong);
            }
        }
        return "Successfully added to playlist.";
    }


}
