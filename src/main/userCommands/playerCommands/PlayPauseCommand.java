package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.PodcastPlayInfo;
import main.Main;
import fileio.input.audioEntities.SongPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class PlayPauseCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static PlayPauseCommand instance = null;

    /**
     * Constructs a new PlayPauseCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private for the singleton design pattern
    private PlayPauseCommand(final String command, final String username, final Integer timestamp,
                             final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of PlayPauseCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of PlayPauseCommand.
     */
    public static PlayPauseCommand getInstance(final String command, final String username,
                                               final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new PlayPauseCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the play/pause command.
     *
     * @return The ObjectNode containing information about the play/pause action.
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
     * Executes the play/pause command for a song.
     *
     * @param user The user for whom the play/pause action is performed.
     * @return A message indicating the result of the play/pause action for the song.
     */
    public String executePlayPauseSong(final NormalUser user) {
        if (user.getLastLoadedSongPlayInfo().getSong() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        // check if we are switching from play to pause
        if (!user.getLastLoadedSongPlayInfo().getSongPaused()) {
            SongPlayInfo songPlayInfo = user.getLastLoadedSongPlayInfo();
            songPlayInfo.updateSongStatus(user, this.timestamp);

            // check if the song has finished meanwhile
            if (user.getLastLoadedSongPlayInfo() == null) {
                return "Please load a source before attempting to pause or resume playback.";
            }

            user.getLastLoadedSongPlayInfo().setSongPaused(true);
            return "Playback paused successfully.";
        }

        // if we are here, it means that we are switching from pause to play
        user.getLastLoadedSongPlayInfo().setLastPlayTimestamp(this.timestamp);
        user.getLastLoadedSongPlayInfo().setSongPaused(false);
        return "Playback resumed successfully.";
    }

    /**
     * Executes the play/pause command for a podcast.
     *
     * @param user The user for whom the play/pause action is performed.
     * @return A message indicating the result of the play/pause action for the podcast.
     */
    public String executePlayPausePodcast(final NormalUser user) {
        // check if I am switching from play to pause
        if (!user.getLastLoadedPodcast().getPodcastPaused()) {
            // episode is currently playing
            PodcastPlayInfo podcast = user.getLastLoadedPodcast();
            podcast.updatePodcastStatus(user, this.timestamp);

            // check if the podcast has finished meanwhile
            if (user.getLastLoadedPodcast() == null) {
                return "Please load a source before attempting to pause or resume playback.";
            }

            user.getLastLoadedPodcast().setPodcastPaused(true);
            return "Playback paused successfully.";
        } else {
            // episode is currently paused
            user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
            user.getLastLoadedPodcast().setPodcastPaused(false);
            return "Playback resumed successfully.";
        }
    }

    /**
     * Executes the play/pause command for a playlist.
     *
     * @param user The user for whom the play/pause action is performed.
     * @return A message indicating the result of the play/pause action for the playlist.
     */
    public String executePlayPausePlaylist(final NormalUser user) {
        if (user.getLastLoadedPlaylist() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        if (!user.getLastLoadedPlaylist().getPaused()) {
            // the playlist is currently playing
            Playlist playList = user.getLastLoadedPlaylist();

            // check playlist shuffle status
            if (!playList.getShuffleStatus()) {
                ArrayList<SongPlayInfo> playlistSongs;
                playlistSongs = user.getLastLoadedPlaylist().getPlaylistSongs();
                playList.updatePlaylistStatus(user, timestamp, playlistSongs);
            } else {
                ArrayList<SongPlayInfo> shuffledSongs;
                shuffledSongs = user.getLastLoadedPlaylist().getShuffledPlaylistSongs();
                playList.updatePlaylistStatus(user, timestamp, shuffledSongs);
            }

            // check if the playlist has finished meanwhile
            if (user.getLastLoadedPlaylist() == null) {
                return "Please load a source before attempting to pause or resume playback.";
            }

            user.getLastLoadedPlaylist().setPaused(true);
            return "Playback paused successfully.";
        } else {
            // playlist is currently paused
            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
            user.getLastLoadedPlaylist().setPaused(false);
            return "Playback resumed successfully.";
        }
    }

    /**
     * Executes the play/pause command for an album.
     *
     * @param user The user for whom the play/pause action is performed.
     * @return A message indicating the result of the play/pause action for the album.
     */
    public String executePlayPauseAlbum(final NormalUser user) {
        if (user.getLastLoadedAlbum() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        if (!user.getLastLoadedAlbum().getPaused()) {
            // the album is currently playing
            Album album = user.getLastLoadedAlbum();

            // check album shuffle status
            if (!album.getShuffleStatus()) {
                ArrayList<SongPlayInfo> albumSongs;
                albumSongs = user.getLastLoadedAlbum().getSongs();
                album.updateAlbumStatus(user, timestamp, albumSongs);
            } else {
                ArrayList<SongPlayInfo> shuffledSongs;
                shuffledSongs = user.getLastLoadedAlbum().getShuffledSongs();
                album.updateAlbumStatus(user, timestamp, shuffledSongs);
            }

            // check if the album has finished meanwhile
            if (user.getLastLoadedAlbum() == null) {
                return "Please load a source before attempting to pause or resume playback.";
            }

            user.getLastLoadedAlbum().setPaused(true);
            return "Playback paused successfully.";
        } else {
            // album is currently paused
            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
            user.getLastLoadedAlbum().setPaused(false);
            return "Playback resumed successfully.";
        }
    }

    /**
     * Executes the play/pause command based on the loaded source (song, podcast, or playlist).
     *
     * @return A message indicating the result of the play/pause action.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.getLoaded()) {
                    return "Please load a source before attempting to pause or resume playback.";
                }

                // decide if we are operating a song, podcast or playlist
                if (user.getLastSearchTypeIndicator() == 1) {
                    // song
                    return executePlayPauseSong(user);
                }

                if (user.getLastSearchTypeIndicator() == 2) {
                    //podcast
                    return executePlayPausePodcast(user);
                }

                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    // playlist
                    return executePlayPausePlaylist(user);
                }

                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    // album
                    return executePlayPauseAlbum(user);
                }
            }
        }
        return null;
    }


}
