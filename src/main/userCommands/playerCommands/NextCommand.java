package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.PodcastPlayInfo;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class NextCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static NextCommand instance = null;

    /**
     * Constructs a new NextCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private for singleton implementation
    private NextCommand(final String command, final String username,
                        final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of NextCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of NextCommand.
     */
    public static NextCommand getInstance(final String command, final String username,
                                          final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new NextCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the next track or episode command.
     *
     * @return The ObjectNode containing information about the next track or episode operation.
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
     * Executes the command to skip to the next episode in a podcast.
     *
     * <p>The method handles the different repeat statuses (0, 1, and 2) for podcast playback.
     *
     * @param user The user for whom the next episode command is executed.
     * @return A message indicating the success of skipping to the next episode.
     */
    public String executePodcastNext(final NormalUser user) {
        PodcastPlayInfo podcastPlayInfo = user.getLastLoadedPodcast();
        Integer currentEpisodeIndex = podcastPlayInfo.getCurrentEpisodeIndex();
        if (user.getLastLoadedPodcast().getRepeatStatus() == 0) {
            if (podcastPlayInfo.getPodcast().getEpisodes().size() <= currentEpisodeIndex + 1) {
                // we have reached the end og the podcast. No next episode
                podcastPlayInfo.setPodcastPaused(true);
                user.setLastLoadedPodcast(null);
                return "Please load a source before skipping to the next track.";
            }
            user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
            user.getLastLoadedPodcast().setCurrentEpisodeIndex(currentEpisodeIndex + 1);
            currentEpisodeIndex++;
            EpisodeInput episode;
            episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
            String currentTrackName = episode.getName();
            user.getLastLoadedPodcast().setPodcastPaused(false);
            user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
            return "Skipped to next track successfully. The current track is "
                    + currentTrackName + ".";
        }
        if (user.getLastLoadedPodcast().getRepeatStatus() == 1) {
            if (podcastPlayInfo.getPodcast().getEpisodes().size() > currentEpisodeIndex + 1) {
                user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
                user.getLastLoadedPodcast().setCurrentEpisodeIndex(currentEpisodeIndex + 1);
                currentEpisodeIndex++;
                EpisodeInput episode;
                episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
                String currentTrackName = episode.getName();
                user.getLastLoadedPodcast().setPodcastPaused(false);
                user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
                user.getLastLoadedPodcast().setCurrentEpisodeIndex(0);
                user.getLastLoadedPodcast().setRepeatStatus(0);
                currentEpisodeIndex = 0;
                EpisodeInput episode;
                episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
                String currentTrackName = episode.getName();
                user.getLastLoadedPodcast().setPodcastPaused(false);
                user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        if (user.getLastLoadedPodcast().getRepeatStatus() == 2) {
            if (podcastPlayInfo.getPodcast().getEpisodes().size() > currentEpisodeIndex + 1) {
                user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
                podcastPlayInfo.setCurrentEpisodeIndex(currentEpisodeIndex + 1);
                currentEpisodeIndex++;
                EpisodeInput episode;
                episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
                String currentTrackName = episode.getName();
                user.getLastLoadedPodcast().setPodcastPaused(false);
                user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
                user.getLastLoadedPodcast().setCurrentEpisodeIndex(0);
                currentEpisodeIndex = 0;
                EpisodeInput episode;
                episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
                String currentTrackName = episode.getName();
                user.getLastLoadedPodcast().setPodcastPaused(false);
                user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        return null;
    }

    /**
     * Updates the current song to the next one in a playlist.
     *
     * <p>The method considers the repeat status (0, 1, and 2) for playlist playback.
     *
     * @param user     The user for whom the next song in the playlist is updated.
     * @param songList The list of songs in the playlist.
     * @return A message indicating the success of skipping to the next song.
     */
    public String updateCurrentPlaylistSongToNext(final NormalUser user,
                                          final ArrayList<SongPlayInfo> songList) {
        Playlist playlist = user.getLastLoadedPlaylist();
        Integer currentSongIndex = playlist.getCurrentSongIndex();

        if (user.getLastLoadedPlaylist().getRepeatStatus() == 0) {
            if (songList.size() <= currentSongIndex + 1) {
                // we have reached the end of the playlist. No next song
                playlist.setPaused(true);
                user.setLastLoadedPlaylist(null);
                return "Please load a source before skipping to the next track.";
            }
            user.getLastLoadedPlaylist().setCurrentSongSecond(0);
            user.getLastLoadedPlaylist().setCurrentSongIndex(currentSongIndex + 1);
            currentSongIndex++;
            String currentTrackName = songList.get(currentSongIndex).getSong().getName();
            user.getLastLoadedPlaylist().setPaused(false);
            user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
            return "Skipped to next track successfully. The current track is "
                    + currentTrackName + ".";
        }
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 1) {
            if (songList.size() > currentSongIndex + 1) {
                user.getLastLoadedPlaylist().setCurrentSongSecond(0);
                user.getLastLoadedPlaylist().setCurrentSongIndex(currentSongIndex + 1);
                currentSongIndex++;
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedPlaylist().setPaused(false);
                user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedPlaylist().setCurrentSongSecond(0);
                user.getLastLoadedPlaylist().setCurrentSongIndex(0);
                currentSongIndex = 0;
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedPlaylist().setPaused(false);
                user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 2) {
            if (songList.size() > currentSongIndex + 1) {
                user.getLastLoadedPlaylist().setCurrentSongSecond(0);
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedPlaylist().setPaused(false);
                user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedPlaylist().setCurrentSongSecond(0);
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedPlaylist().setPaused(false);
                user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        return null;
    }

    /**
     * Updates the current song to the next one in an album.
     *
     * <p>The method considers the repeat status (0, 1, and 2) for album playback.
     *
     * @param user     The user for whom the next song in the album is updated.
     * @param songList The list of songs in the album.
     * @return A message indicating the success of skipping to the next song.
     */
    public String updateCurrentAlbumSongToNext(final NormalUser user,
                                                  final ArrayList<SongPlayInfo> songList) {
        Album album = user.getLastLoadedAlbum();
        Integer currentSongIndex = album.getCurrentSongIndex();

        if (user.getLastLoadedAlbum().getRepeatStatus() == 0) {
            if (songList.size() <= currentSongIndex + 1) {
                // we have reached the end of the album. No next song
                album.setPaused(true);
                user.setLastLoadedAlbum(null);
                user.setLoaded(false);
                return "Please load a source before skipping to the next track.";
            }
            user.getLastLoadedAlbum().setCurrentSongSecond(0);
            user.getLastLoadedAlbum().setCurrentSongIndex(currentSongIndex + 1);
            currentSongIndex++;
            String currentTrackName = songList.get(currentSongIndex).getSong().getName();
            user.getLastLoadedAlbum().setPaused(false);
            user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
            return "Skipped to next track successfully. The current track is "
                    + currentTrackName + ".";
        }
        if (user.getLastLoadedAlbum().getRepeatStatus() == 1) {
            if (songList.size() > currentSongIndex + 1) {
                user.getLastLoadedAlbum().setCurrentSongSecond(0);
                user.getLastLoadedAlbum().setCurrentSongIndex(currentSongIndex + 1);
                currentSongIndex++;
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedAlbum().setPaused(false);
                user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedAlbum().setCurrentSongSecond(0);
                user.getLastLoadedAlbum().setCurrentSongIndex(0);
                currentSongIndex = 0;
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedAlbum().setPaused(false);
                user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        if (user.getLastLoadedAlbum().getRepeatStatus() == 2) {
            if (songList.size() > currentSongIndex + 1) {
                user.getLastLoadedAlbum().setCurrentSongSecond(0);
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedAlbum().setPaused(false);
                user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            } else {
                user.getLastLoadedAlbum().setCurrentSongSecond(0);
                String currentTrackName = songList.get(currentSongIndex).getSong().getName();
                user.getLastLoadedAlbum().setPaused(false);
                user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                return "Skipped to next track successfully. The current track is "
                        + currentTrackName + ".";
            }
        }
        return null;
    }

    /**
     * Executes the command to skip to the next track in a playlist.
     *
     * <p>The method delegates to the appropriate update method based on the
     * playlist's shuffle status.
     *
     * @param user The user for whom the next track command is executed.
     * @return A message indicating the success of skipping to the next track in the playlist.
     */
    public String executePlaylistNext(final NormalUser user) {
        Playlist playlist = user.getLastLoadedPlaylist();
        if (!playlist.getShuffleStatus()) {
            return updateCurrentPlaylistSongToNext(user, playlist.getPlaylistSongs());
        } else {
            return updateCurrentPlaylistSongToNext(user, playlist.getShuffledPlaylistSongs());
        }
    }

    /**
     * Executes the command to skip to the next track in an album.
     *
     * <p>The method delegates to the appropriate update method based on the
     * album's shuffle status.
     *
     * @param user The user for whom the next track command is executed.
     * @return A message indicating the success of skipping to the next track in the album.
     */
    public String executeAlbumNext(final NormalUser user) {
        Album album = user.getLastLoadedAlbum();
        if (!album.getShuffleStatus()) {
            return updateCurrentAlbumSongToNext(user, album.getSongs());
        } else {
            return updateCurrentAlbumSongToNext(user, album.getShuffledSongs());
        }
    }

    /**
     * Retrieves the message based on the user's last loaded source and
     * executes the corresponding next command.
     *
     * <p>The method checks if the user had previously loaded anything and then delegates to the
     * appropriate handler for playlist or podcast.
     *
     * @return A message indicating the success of skipping to the next track or episode.
     */
    public String getMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before skipping to the next track.";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                    user.getLastLoadedPlaylist().setPaused(false);
                    return executePlaylistNext(user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
                    user.getLastLoadedAlbum().setPaused(false);
                    return executeAlbumNext(user);
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                    return executePodcastNext(user);
                }
            }
        }
        return null;
    }


}
