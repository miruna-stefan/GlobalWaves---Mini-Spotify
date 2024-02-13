package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import fileio.input.audioEntities.PodcastPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.Artist;
import users.Host;
import users.NormalUser;

public final class LoadCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static LoadCommand instance = null;

    /**
     * Constructs a new LoadCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private in order to implement singleton design pattern
    private LoadCommand(final String command, final String username,
                        final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of LoadCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of LoadCommand.
     */
    public static LoadCommand getInstance(final String command, final String username,
                                          final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new LoadCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Generates a JSON node containing the results of the load command.
     *
     * @return the JSON node representing the load command results.
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
     * Updates the host's list of listeners for the given podcast.
     *
     * @param podcastPlayInfo The podcast play information.
     * @param user            The user associated with the playback.
     */
    public void updateHostsListOfListeners(final PodcastPlayInfo podcastPlayInfo,
                                           final NormalUser user) {
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(podcastPlayInfo.getPodcast().getOwner())) {
                // check if the user is already in the list
                if (!host.getListeners().contains(user)) {
                    host.getListeners().add(user);
                }
                break;
            }
        }
    }

    /**
     * Loads a song into the user's player.
     *
     * @param user The user associated with the playback.
     */
    public void loadSong(final NormalUser user) {

        user.setLastLoadTypeIndicator(TYPE_SONG);
        user.setLastLoadedSongPlayInfo(user.getLastSelectedSong());
        user.getLastLoadedSongPlayInfo().setSong(user.getLastSelectedSong().getSong());
        user.getLastLoadedSongPlayInfo().setLastPlayTimestamp(this.timestamp);
        user.getLastLoadedSongPlayInfo().setRepeatStatus(0);
        user.getLastLoadedSongPlayInfo().setSongPaused(false);
        user.getLastLoadedSongPlayInfo().setCurrentSecond(0);
        user.setLastSelectedSong(null);
        user.updateEverythingForSong(user.getLastLoadedSongPlayInfo());
        if (user.getIsPremium()) {
            user.updateSongAndArtistForPremiumUser(user.getLastLoadedSongPlayInfo());
        }

        updateArtistHadSomethingOnPlay(user.getLastLoadedSongPlayInfo());
    }

    /**
     * Loads a podcast into the user's player.
     *
     * @param user The user associated with the playback.
     */
    public void loadPodcast(final NormalUser user) {
        user.setLastLoadTypeIndicator(TYPE_PODCAST);

        // check if the podcast had been previously loaded
        Boolean found = false;
        if (!user.getPodcastPlayInfoList().isEmpty()) {
            for (PodcastPlayInfo podcastPlayInfo : user.getPodcastPlayInfoList()) {
                String selectedPodcastName = user.getLastSelectedPodcast().getName();
                if (podcastPlayInfo.getPodcast().getName()
                        .equals(selectedPodcastName)) {
                    found = true;
                    user.setLastLoadedPodcast(podcastPlayInfo);
                    user.updateWrappedEpisodes(user.getLastLoadedPodcast().
                            getPodcast().getEpisodes().get(user.getLastLoadedPodcast().
                                    getCurrentEpisodeIndex()));
                    break;
                }
            }
        }

        /* if the selected podcast has never been loaded before,
        instantiate a new PodcastPlayInfo object and add it to the list */
        if (!found) {
            PodcastPlayInfo podcastPlayInfo;
            podcastPlayInfo = new PodcastPlayInfo(user.getLastSelectedPodcast(), 0, 0);
            user.getPodcastPlayInfoList().add(podcastPlayInfo);
            user.setLastLoadedPodcast(podcastPlayInfo);
            user.updateWrappedEpisodes(podcastPlayInfo.getPodcast().
                    getEpisodes().get(0));

            /* we only need to update the host's list of listeners if the podcast
            is loaded for the first time in the user's player */
            updateHostsListOfListeners(user.getLastLoadedPodcast(), user);
        }

        user.getLastLoadedPodcast().setLastPlayTimestamp(this.timestamp);
        user.getLastLoadedPodcast().setPodcastPaused(false);
        user.setLastSelectedPodcast(null);
    }

    /**
     * Loads a playlist into the user's player.
     *
     * @param user The user associated with the playback.
     */
    public void loadPlaylist(final NormalUser user) {
        user.setLastLoadTypeIndicator(TYPE_PLAYLIST);
        user.setLastLoadedPlaylist(user.getLastSelectedPlaylist());
        user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
        user.getLastLoadedPlaylist().setPaused(false);
        user.getLastLoadedPlaylist().setShuffleStatus(false);

        user.setLastSelectedPlaylist(null);
        if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
            user.updateEverythingForSong(user.
                    getLastLoadedPlaylist().getPlaylistSongs().get(0));
            updateArtistHadSomethingOnPlay(user.getLastLoadedPlaylist().
                    getPlaylistSongs().get(0));
        } else {
            user.updateEverythingForSong(user.getLastLoadedPlaylist().
                    getShuffledPlaylistSongs().get(0));
            updateArtistHadSomethingOnPlay(user.getLastLoadedPlaylist().
                    getShuffledPlaylistSongs().get(0));
        }

        if (user.getIsPremium()) {
            user.updateSongAndArtistForPremiumUser(user.getLastLoadedPlaylist().
                    getPlaylistSongs().get(0));
        }
    }

    /**
     * Loads an album into the user's player.
     *
     * @param user The user associated with the playback.
     */
    public void loadAlbum(final NormalUser user) {
        user.setLastLoadTypeIndicator(TYPE_ALBUM);
        user.setLastLoadedAlbum(user.getLastSelectedAlbum());
        user.getLastLoadedAlbum().setLastPlayTimestamp(this.timestamp);
        user.getLastLoadedAlbum().setCurrentSongIndex(0);
        user.getLastLoadedAlbum().setCurrentSongSecond(0);
        user.getLastLoadedAlbum().setPaused(false);
        user.getLastLoadedAlbum().setShuffleStatus(false);

        user.setLastSelectedAlbum(null);
        if (!user.getLastLoadedAlbum().getShuffleStatus()) {
            user.updateEverythingForSong(user.getLastLoadedAlbum().getSongs().get(0));
        } else {
            user.updateEverythingForSong(user.getLastLoadedAlbum().
                    getShuffledSongs().get(0));
        }

        if (user.getIsPremium()) {
            user.updateSongAndArtistForPremiumUser(user.getLastLoadedAlbum().
                    getSongs().get(0));
        }

        // update the field which indicates that the artist has had something on play
        for (Artist artist : Main.artistsList) {
            for (Album album : artist.getAlbums()) {
                if (album.getName().equals(user.getLastLoadedAlbum().getName())) {
                    artist.setHadSomethingOnPlay(true);
                    break;
                }
            }
        }
    }


    /**
     * Executes the load command based on the last search type and selected audio entity.
     *
     * @return a String message indicating the result of the load operation.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                // check if the user has previously made any selection
                if (!user.getSelected()) {
                    return "Please select a source before attempting to load.";
                }

                if (user.getLastSearchTypeIndicator() == TYPE_SONG) {
                    // song
                    if (user.getLastSelectedSong() == null) {
                        return "Please select a source before attempting to load.";
                    }
                    loadSong(user);
                }

                if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                    // podcast
                    if (user.getLastSelectedPodcast() == null) {
                        return "Please select a source before attempting to load.";
                    }
                    loadPodcast(user);
                }

                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    // playlist
                    if (user.getLastSelectedPlaylist() == null) {
                        return "Please select a source before attempting to load.";
                    }
                    if (user.getLastSelectedPlaylist().getPlaylistSongs().isEmpty()) {
                        return "You can't load an empty audio collection!";
                    }
                    loadPlaylist(user);
                }

                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    // album
                    if (user.getLastSelectedAlbum() == null) {
                        return "Please select a source before attempting to load.";
                    }
                    if (user.getLastSelectedAlbum().getSongs().isEmpty()) {
                        return "You can't load an empty audio collection!";
                    }
                    loadAlbum(user);
                }

                user.setLoaded(true);
                user.setSelected(false);
                user.setSearched(false);
            }
        }
        return "Playback loaded successfully.";
    }


}
