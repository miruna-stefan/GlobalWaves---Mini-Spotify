package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.PodcastPlayInfo;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class StatusCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static StatusCommand instance = null;

    /**
     * Constructs a new StatusCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    private StatusCommand(final String command, final String username,
                          final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of StatusCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of StatusCommand.
     */
    public static StatusCommand getInstance(final String command, final String username,
                                            final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new StatusCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp
     * and playback status information.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                if (!user.getLoaded()) {
                    ObjectNode nodeStats = JsonNodeFactory.instance.objectNode();
                    return printfDefault(node, nodeStats);
                }
                ObjectNode nodeStats = JsonNodeFactory.instance.objectNode();
                if (user.getLastSearchTypeIndicator() == TYPE_SONG) {
                    return this.executeStatusOnSong(node, nodeStats, user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                    return this.executeStatusOnPodcast(node, nodeStats, user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    return this.executeStatusOnPlaylist(node, nodeStats, user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    return this.executeStatusOnAlbum(node, nodeStats, user);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the repeat status of an audio file.
     *
     * @param songInfo The SongPlayInfo object containing information about the audio file.
     * @return A string representing the repeat status ("No Repeat," "Repeat Once,"
     * or "Repeat Infinite").
     */
    public String getRepeatStatusAudioFile(final SongPlayInfo songInfo) {
        if (songInfo.getSong() == null) {
            return "No Repeat";
        }
        if (songInfo.getRepeatStatus() == 0) {
            return "No Repeat";
        }
        if (songInfo.getRepeatStatus() == 1) {
            return "Repeat Once";
        }
        if (songInfo.getRepeatStatus() == 2) {
            return "Repeat Infinite";
        }
        return "No Repeat";
    }

    /**
     * Retrieves the repeat status of a playlist.
     *
     * @param playlist The Playlist object containing information about the playlist.
     * @return A string representing the repeat status ("No Repeat," "Repeat All,"
     * or "Repeat Current Song").
     */
    public String getRepeatPlaylist(final Playlist playlist) {
        if (playlist.getRepeatStatus() == null) {
            return "No Repeat";
        }
        if (playlist.getRepeatStatus() == 0) {
            return "No Repeat";
        }
        if (playlist.getRepeatStatus() == 1) {
            return "Repeat All";
        }
        if (playlist.getRepeatStatus() == 2) {
            return "Repeat Current Song";
        }
        return "No Repeat";
    }

    /**
     * Retrieves the repeat status of an album.
     *
     * @param album The Album object containing information about the album.
     * @return A string representing the repeat status ("No Repeat," "Repeat All,"
     * or "Repeat Current Song").
     */
    public String getRepeatAlbum(final Album album) {
        if (album.getRepeatStatus() == null) {
            return "No Repeat";
        }
        if (album.getRepeatStatus() == 0) {
            return "No Repeat";
        }
        if (album.getRepeatStatus() == 1) {
            return "Repeat All";
        }
        if (album.getRepeatStatus() == 2) {
            return "Repeat Current Song";
        }
        return "No Repeat";
    }

    /**
     * Retrieves the repeat status of a podcast.
     *
     * @param podcastPlayInfo The PodcastPlayInfo object containing information about the podcast.
     * @return A string representing the repeat status ("No Repeat," "Repeat All,"
     * or "Repeat Current Song").
     */
    public String getRepeatPodcast(final PodcastPlayInfo podcastPlayInfo) {
        if (podcastPlayInfo.getRepeatStatus() == null) {
            return "No Repeat";
        }
        if (podcastPlayInfo.getRepeatStatus() == 0) {
            return "No Repeat";
        }
        if (podcastPlayInfo.getRepeatStatus() == 1) {
            return "Repeat All";
        }
        if (podcastPlayInfo.getRepeatStatus() == 2) {
            return "Repeat Current Song";
        }
        return "No Repeat";
    }

    /**
     * Prints the default status information to the provided ObjectNode.
     *
     * @param node      The ObjectNode to store default status information.
     * @param nodeStats The ObjectNode to store statistics information.
     * @return The modified ObjectNode containing the default status information.
     */
    ObjectNode printfDefault(final ObjectNode node, final ObjectNode nodeStats) {
        nodeStats.put("name", "");
        nodeStats.put("remainedTime", 0);
        nodeStats.put("repeat", "No Repeat");
        nodeStats.put("shuffle", false);
        nodeStats.put("paused", true);
        node.putPOJO("stats", nodeStats);
        return node;
    }


    /**
     * Retrieves the name of the currently loaded song for the user.
     *
     * @return The name of the currently loaded song.
     */
    public String getName() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (user.getLastSearchTypeIndicator() == 1) {
                    // song
                    return user.getLastLoadedSongPlayInfo().getSong().getName();
                }
            }
        }
        return null;
    }


    /**
     * Retrieves the shuffle status of a podcast or song,
     * which is always false, because shuffle can only be applied to playlists.
     *
     * @return Always false.
     */
    public Boolean getShuffleStatusIfNotPlaylist() {
        return false;
    }

    /**
     * Prints the playback status of a song to the provided ObjectNode.
     *
     * @param node      The ObjectNode to store the song status information.
     * @param nodeStats The ObjectNode to store statistics information.
     * @param user      The NormalUser object for the current user.
     * @return The modified ObjectNode containing the song status information.
     */
    public ObjectNode executeStatusOnSong(final ObjectNode node, final ObjectNode nodeStats,
                                      final NormalUser user) {
        if (user.getLastLoadedSongPlayInfo() == null) {
            return printfDefault(node, nodeStats);
        }
        if (!user.getConnectionStatus()) {
            getSongStats(node, nodeStats, user);
            return node;
        }

        if (!user.isSongStillLoaded(this.timestamp)) {
            return printfDefault(node, nodeStats);
        } else {
            getSongStats(node, nodeStats, user);
            return node;
        }
    }

    /**
     * Retrieves and prints the statistics of a song for the provided ObjectNode.
     *
     * @param nodeStats The ObjectNode to store the statistics information.
     * @param user      The NormalUser object for the current user.
     */
    public void getSongStats(final ObjectNode node, final ObjectNode nodeStats,
                                final NormalUser user) {
        SongPlayInfo songInfo = user.getLastLoadedSongPlayInfo();
        nodeStats.put("name", songInfo.getSong().getName());
        Integer remainedTime = songInfo.getSong().getDuration() - songInfo.getCurrentSecond();
        nodeStats.put("remainedTime", remainedTime);
        nodeStats.put("repeat", getRepeatStatusAudioFile(user.getLastLoadedSongPlayInfo()));
        nodeStats.put("shuffle", getShuffleStatusIfNotPlaylist());
        nodeStats.put("paused", songInfo.getSongPaused());
        node.putPOJO("stats", nodeStats);

    }

    /**
     * Prints the playback status of a podcast to the provided ObjectNode.
     *
     * @param node      The ObjectNode to store the podcast status information.
     * @param nodeStats The ObjectNode to store statistics information.
     * @param user      The NormalUser object for the current user.
     * @return The modified ObjectNode containing the podcast status information.
     */
    public ObjectNode executeStatusOnPodcast(final ObjectNode node,
                                         final ObjectNode nodeStats, final NormalUser user) {
        if (user.getLastLoadedPodcast() == null) {
            return printfDefault(node, nodeStats);
        }

        if (!user.getConnectionStatus()) {
            getPodcastStats(node, nodeStats, user);
            return node;
        }

        if (!user.isPodcastStillLoaded(this.timestamp)) {
            return printfDefault(node, nodeStats);
        } else {
            getPodcastStats(node, nodeStats, user);
            return node;
        }
    }

    /**
     * Retrieves and prints the statistics of a podcast for the provided ObjectNode.
     *
     * @param nodeStats The ObjectNode to store the statistics information.
     * @param user      The NormalUser object for the current user.
     */
    public void getPodcastStats(final ObjectNode node, final ObjectNode nodeStats,
                                final NormalUser user) {
        Integer currentEpisodeIndex = user.getLastLoadedPodcast().getCurrentEpisodeIndex();
        ArrayList<EpisodeInput> episodes;
        episodes = user.getLastLoadedPodcast().getPodcast().getEpisodes();
        nodeStats.put("name", episodes.get(currentEpisodeIndex).getName());
        Integer episodeDuration = episodes.get(currentEpisodeIndex).getDuration();
        Integer currentSecondEpisode = user.getLastLoadedPodcast().getCurrentSecondEpisode();
        nodeStats.put("remainedTime", episodeDuration - currentSecondEpisode);
        nodeStats.put("repeat", getRepeatPodcast(user.getLastLoadedPodcast()));
        nodeStats.put("shuffle", getShuffleStatusIfNotPlaylist());
        nodeStats.put("paused", user.getLastLoadedPodcast().getPodcastPaused());
        node.putPOJO("stats", nodeStats);

    }

    /**
     * Retrieves and prints the statistics of a playlist for the provided ObjectNode.
     *
     * @param nodeStats The ObjectNode to store the statistics information.
     * @param user      The NormalUser object for the current user.
     * @param songs     The list of songs in the playlist.
     */
    public void getPlaylistStats(final ObjectNode node, final ObjectNode nodeStats,
                                 final NormalUser user, final ArrayList<SongPlayInfo> songs) {
        Integer currentSongIndex = user.getLastLoadedPlaylist().getCurrentSongIndex();
        Integer currentSongSecond = user.getLastLoadedPlaylist().getCurrentSongSecond();
        Integer currentSongDuration = songs.get(currentSongIndex).getSong().getDuration();
        nodeStats.put("name", songs.get(currentSongIndex).getSong().getName());
        nodeStats.put("remainedTime", currentSongDuration - currentSongSecond);
        nodeStats.put("repeat", getRepeatPlaylist(user.getLastLoadedPlaylist()));
        nodeStats.put("shuffle", user.getLastLoadedPlaylist().getShuffleStatus());
        nodeStats.put("paused", user.getLastLoadedPlaylist().getPaused());
        node.putPOJO("stats", nodeStats);
    }


    /**
     * Prints the playback status of a playlist to the provided ObjectNode.
     *
     * @param node      The ObjectNode to store the playlist status information.
     * @param nodeStats The ObjectNode to store statistics information.
     * @param user      The NormalUser object for the current user.
     * @return The modified ObjectNode containing the playlist status information.
     */
    public ObjectNode executeStatusOnPlaylist(final ObjectNode node, final ObjectNode nodeStats,
                                          final NormalUser user) {
        if (user.getLastLoadedPlaylist() == null) {
            return printfDefault(node, nodeStats);
        }
        if (!user.getConnectionStatus()) {
            if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                getPlaylistStats(node, nodeStats, user,
                        user.getLastLoadedPlaylist().getPlaylistSongs());
            } else {


                getPlaylistStats(node, nodeStats, user,
                        user.getLastLoadedPlaylist().getShuffledPlaylistSongs());
            }
            return node;
        }

        if (!user.isPlaylistStillLoaded(this.timestamp)) {
            return printfDefault(node, nodeStats);
        } else {
            if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                getPlaylistStats(node, nodeStats, user,
                        user.getLastLoadedPlaylist().getPlaylistSongs());
            } else {
                getPlaylistStats(node, nodeStats, user,
                        user.getLastLoadedPlaylist().getShuffledPlaylistSongs());
            }
            return node;
        }
    }

    /**
     * Executes the status command on an album.
     *
     * @param node      The ObjectNode to store the album status information.
     * @param nodeStats The ObjectNode to store statistics information.
     * @param user      The NormalUser object for the current user.
     * @return The modified ObjectNode containing the album status information.
     */
    public ObjectNode executeStatusOnAlbum(final ObjectNode node, final ObjectNode nodeStats,
                                              final NormalUser user) {
        if (user.getLastLoadedAlbum() == null) {
            return printfDefault(node, nodeStats);
        }
        if (!user.getConnectionStatus()) {
            if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                getAlbumStats(node, nodeStats, user, user.getLastLoadedAlbum().getSongs());
            } else {
                getAlbumStats(node, nodeStats, user, user.getLastLoadedAlbum().getShuffledSongs());
            }
            return node;
        }

        if (!user.isAlbumStillLoaded(this.timestamp)) {
            return printfDefault(node, nodeStats);
        } else {
            if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                getAlbumStats(node, nodeStats, user, user.getLastLoadedAlbum().getSongs());
            } else {
                getAlbumStats(node, nodeStats, user, user.getLastLoadedAlbum().getShuffledSongs());
            }
            return node;
        }
    }

    /**
     * Retrieves and prints the statistics of an album for the provided ObjectNode.
     *
     * @param nodeStats The ObjectNode to store the statistics information.
     * @param user      The NormalUser object for the current user.
     * @param songs     The list of songs in the album.
     */
    public void getAlbumStats(final ObjectNode node, final ObjectNode nodeStats,
                              final NormalUser user, final ArrayList<SongPlayInfo> songs) {
        Integer currentSongIndex = user.getLastLoadedAlbum().getCurrentSongIndex();
        Integer currentSongSecond = user.getLastLoadedAlbum().getCurrentSongSecond();
        Integer currentSongDuration = songs.get(currentSongIndex).getSong().getDuration();
        nodeStats.put("name", songs.get(currentSongIndex).getSong().getName());
        nodeStats.put("remainedTime", currentSongDuration - currentSongSecond);
        nodeStats.put("repeat", getRepeatAlbum(user.getLastLoadedAlbum()));
        nodeStats.put("shuffle", user.getLastLoadedAlbum().getShuffleStatus());
        nodeStats.put("paused", user.getLastLoadedAlbum().getPaused());
        node.putPOJO("stats", nodeStats);
    }


}
