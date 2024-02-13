package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class ShuffleCommand extends StandardCommandForUserPlayer {
    private Integer seed;

    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static ShuffleCommand instance = null;

    /**
     * Gets the shuffle seed.
     *
     * @return The shuffle seed.
     */
    public Integer getSeed() {
        return seed;
    }

    /**
     * Sets the shuffle seed.
     *
     * @param seed The shuffle seed to set.
     */
    public void setSeed(final Integer seed) {
        this.seed = seed;
    }

    /**
     * Constructs a new ShuffleCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param seed      the seed used for shuffling.
     * @param node      the JSON node associated with the command.
     */
    private ShuffleCommand(final String command, final String username, final Integer timestamp,
                           final Integer seed, final ObjectNode node) {
        super(command, timestamp, node, username);
        this.seed = seed;
    }

    /**
     * Gets the singleton instance of ShuffleCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param seed      the seed used for shuffling.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of ShuffleCommand.
     */
    public static ShuffleCommand getInstance(final String command, final String username,
                                             final Integer timestamp, final Integer seed,
                                             final ObjectNode node) {
        if (instance == null) {
            instance = new ShuffleCommand(command, username, timestamp, seed, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setSeed(seed);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the shuffle operation.
     *
     * @return The ObjectNode containing information about the shuffle operation.
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
                    node.put("message", "Please load a source before using the shuffle function.");
                    return node;
                }
                if (user.getLastLoadTypeIndicator() == 1) {
                    node.put("message", "The loaded source is not a playlist or an album.");
                }
                if (user.getLastLoadTypeIndicator() == 2) {
                    node.put("message", "The loaded source is not a playlist or an album.");
                }
                if (user.getLastLoadTypeIndicator() == TYPE_PLAYLIST) {
                    if (user.getLastLoadedPlaylist() == null) {
                        node.put("message",
                                "Please load a source before using the shuffle function.");
                        return node;
                    }
                    if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                        // we need to switch to shuffle mode
                        return this.activateShuffleModeForPlaylist(user);
                    } else {
                        // we need to unshuffle the playlist
                        return this.deactivateShuffleModeForPlaylist(user);
                    }

                }
                if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
                    if (user.getLastLoadedAlbum() == null) {
                        node.put("message",
                                "Please load a source before using the shuffle function.");
                        return node;
                    }
                    if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                        // we need to switch to shuffle mode
                        return this.activateShuffleModeForAlbum(user);
                    } else {
                        // we need to unshuffle the album
                        return this.deactivateShuffleModeForAlbum(user);
                    }

                }

            }
        }
        return node;
    }


    /**
     * Creates a shuffled list of songs for the specified playlist.
     *
     * @param user The user for whom the playlist is shuffled.
     * @param shuffleSeed The seed used for shuffling the playlist.
     * @return The shuffled list of songs.
     */
    public ArrayList<SongPlayInfo> createShuffledSongListForPlaylist(final NormalUser user,
                                                          final Integer shuffleSeed) {
        ArrayList<SongPlayInfo> shuffledSongList = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : user.getLastLoadedPlaylist().getPlaylistSongs()) {
            shuffledSongList.add(songPlayInfo);
        }
        Collections.shuffle(shuffledSongList, new Random(shuffleSeed));
        return shuffledSongList;
    }

    /**
     * Creates a shuffled list of songs for the specified album.
     *
     * @param user The user for whom the album is shuffled.
     * @param shuffleSeed The seed used for shuffling the album.
     * @return The shuffled list of songs.
     */
    public ArrayList<SongPlayInfo> createShuffledSongListForAlbum(final NormalUser user,
                                                                     final Integer shuffleSeed) {
        ArrayList<SongPlayInfo> shuffledSongList = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : user.getLastLoadedAlbum().getSongs()) {
            shuffledSongList.add(songPlayInfo);
        }
        Collections.shuffle(shuffledSongList, new Random(shuffleSeed));
        return shuffledSongList;
    }

    /**
     * Updates the index to the shuffled list for the current song.
     *
     * @param user The user for whom the index is updated.
     */
    void updateIndextoShuffledListForPlaylist(final NormalUser user) {
        Playlist playlist = user.getLastLoadedPlaylist();
        Integer currentSongIdx = playlist.getCurrentSongIndex();
        String currentSongName = playlist.getPlaylistSongs().get(currentSongIdx)
                .getSong().getName();
        for (int i = 0; i < playlist.getShuffledPlaylistSongs().size(); i++) {
            String shuffledSongName;
            shuffledSongName = playlist.getShuffledPlaylistSongs().get(i).getSong().getName();
            if (shuffledSongName.equals(currentSongName)) {
                user.getLastLoadedPlaylist().setCurrentSongIndex(i);
            }
        }
    }

    /**
     * Updates the index to the shuffled list for the current song.
     *
     * @param user The user for whom the index is updated.
     */
    void updateIndextoShuffledListForAlbum(final NormalUser user) {
        Album album = user.getLastLoadedAlbum();
        Integer currentSongIdx = album.getCurrentSongIndex();
        String currentSongName = album.getSongs().get(currentSongIdx).getSong().getName();
        for (int i = 0; i < album.getShuffledSongs().size(); i++) {
            String shuffledSongName;
            shuffledSongName = album.getShuffledSongs().get(i).getSong().getName();
            if (shuffledSongName.equals(currentSongName)) {
                user.getLastLoadedAlbum().setCurrentSongIndex(i);
            }
        }
    }

    /**
     * Gets the index back to the unshuffled list for the current song.
     *
     * @param user The user for whom the index is updated.
     */
    void getIndexBackToUnshuffledListForPlaylist(final NormalUser user) {
        Playlist playlist = user.getLastLoadedPlaylist();
        Integer currentSongIdx = playlist.getCurrentSongIndex();
        String currentSongName = playlist.getShuffledPlaylistSongs()
                .get(currentSongIdx).getSong().getName();
        for (int i = 0; i < playlist.getPlaylistSongs().size(); i++) {
            String unshuffledSongName;
            unshuffledSongName = playlist.getPlaylistSongs().get(i).getSong().getName();
            if (unshuffledSongName.equals(currentSongName)) {
                user.getLastLoadedPlaylist().setCurrentSongIndex(i);
            }
        }
    }

    /**
     * Gets the index back to the unshuffled list for the current song.
     *
     * @param user The user for whom the index is updated.
     */
    void getIndexBackToUnshuffledListForAlbum(final NormalUser user) {
        Album album = user.getLastLoadedAlbum();
        Integer currentSongIdx = album.getCurrentSongIndex();
        String currentSongName = album.getShuffledSongs().get(currentSongIdx).getSong().getName();
        for (int i = 0; i < album.getSongs().size(); i++) {
            String unshuffledSongName;
            unshuffledSongName = album.getSongs().get(i).getSong().getName();
            if (unshuffledSongName.equals(currentSongName)) {
                user.getLastLoadedAlbum().setCurrentSongIndex(i);
            }
        }
    }

    /**
     * Activates shuffle mode for the user's playlist.
     *
     * <p>This method shuffles the songs in the playlist, updates the current song index
     * in the shuffled array, and sets the shuffle status to true.
     *
     * @param user The user for whom shuffle mode is activated.
     * @return The ObjectNode containing information about the shuffle operation.
     * @throws NullPointerException If the playlist or its songs are null.
     */
    public ObjectNode activateShuffleModeForPlaylist(final NormalUser user) {
        Playlist playlist = user.getLastLoadedPlaylist();
        playlist.setShuffledPlaylistSongs(createShuffledSongListForPlaylist(user, this.seed));

        // identify the current song index in the shuffled array
        updateIndextoShuffledListForPlaylist(user);

        user.getLastLoadedPlaylist().setShuffleStatus(true);
        node.put("message", "Shuffle function activated successfully.");
        return node;
    }

    /**
     * Activates shuffle mode for the user's album.
     *
     * <p>This method shuffles the songs in the album, updates the current song index
     * in the shuffled array, and sets the shuffle status to true.
     *
     * @param user The user for whom shuffle mode is activated.
     * @return The ObjectNode containing information about the shuffle operation.
     * @throws NullPointerException If the album or its songs are null.
     */
    public ObjectNode activateShuffleModeForAlbum(final NormalUser user) {
        Album album = user.getLastLoadedAlbum();
        album.setShuffledSongs(createShuffledSongListForAlbum(user, this.seed));

        // identify the current song index in the shuffled array
        updateIndextoShuffledListForAlbum(user);

        user.getLastLoadedAlbum().setShuffleStatus(true);
        node.put("message", "Shuffle function activated successfully.");
        return node;
    }

    /**
     * Deactivates shuffle mode for the user's playlist.
     *
     * <p>This method updates the current song index in the unshuffled array
     * and sets the shuffle status to false.
     *
     * @param user The user for whom shuffle mode is deactivated.
     * @return The ObjectNode containing information about the shuffle operation.
     * @throws NullPointerException If the playlist or its songs are null.
     */
    public ObjectNode deactivateShuffleModeForPlaylist(final NormalUser user) {
        if (!user.getLastLoadedPlaylist().getPaused()) {
            ArrayList<SongPlayInfo> shuffledSongs;
            shuffledSongs = user.getLastLoadedPlaylist().getShuffledPlaylistSongs();
            Playlist playList = user.getLastLoadedPlaylist();
            playList.updatePlaylistStatus(user, this.timestamp, shuffledSongs);
            if (user.getLastLoadedPlaylist() == null) {
                node.put("message",
                        "Please load a source before using the shuffle function.");
                return node;
            }
            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
        }

        // identify the current song index in the unshuffled array
        getIndexBackToUnshuffledListForPlaylist(user);

        user.getLastLoadedPlaylist().setShuffledPlaylistSongs(null);
        user.getLastLoadedPlaylist().setShuffleStatus(false);
        node.put("message", "Shuffle function deactivated successfully.");
        return node;
    }

    /**
     * Deactivates shuffle mode for the user's album.
     *
     * <p>This method updates the current song index in the unshuffled array
     * and sets the shuffle status to false.
     *
     * @param user The user for whom shuffle mode is deactivated.
     * @return The ObjectNode containing information about the shuffle operation.
     * @throws NullPointerException If the album or its songs are null.
     */
    public ObjectNode deactivateShuffleModeForAlbum(final NormalUser user) {
        if (!user.getLastLoadedAlbum().getPaused()) {
            ArrayList<SongPlayInfo> shuffledSongs;
            shuffledSongs = user.getLastLoadedAlbum().getShuffledSongs();
            Album album = user.getLastLoadedAlbum();
            album.updateAlbumStatus(user, this.timestamp, shuffledSongs);
            if (user.getLastLoadedAlbum() == null) {
                node.put("message",
                        "Please load a source before using the shuffle function.");
                return node;
            }
            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
        }

        // identify the current song index in the unshuffled array
        getIndexBackToUnshuffledListForAlbum(user);

        user.getLastLoadedAlbum().setShuffledSongs(null);
        user.getLastLoadedAlbum().setShuffleStatus(false);
        node.put("message", "Shuffle function deactivated successfully.");
        return node;
    }

}
