package users;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.HomePage;
import fileio.input.Pagination.Page;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.PodcastInput;
import fileio.input.audioEntities.PodcastPlayInfo;
import fileio.input.audioEntities.Album;
import fileio.input.entitiesForArtist.Merch;
import main.Main;
import main.adminCommands.VisitableDeletion;
import main.adminCommands.VisitorDeletion;
import main.statisticsCommands.VisitableWrapped;
import main.statisticsCommands.VisitorWrapped;
import fileio.input.wrappedEntities.WrappedArtist;
import fileio.input.wrappedEntities.WrappedGenre;
import fileio.input.wrappedEntities.WrappedAlbum;
import fileio.input.wrappedEntities.WrappedSong;
import fileio.input.wrappedEntities.WrappedEpisode;
import fileio.input.wrappedEntities.MonetizedSong;


import java.util.ArrayList;

public class NormalUser extends GeneralUser implements VisitableDeletion,
        VisitableWrapped, Observer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;
    private static final int MONETIZATION_POWER_BASE = 10;
    private static final int MONETIZATION_POWER_EXPONENT = 6;
    private static final double HUNDRED = 100.0;

    /* ArrayLists containing the results of the last search command
    for each type of audio entity */
    private ArrayList<SongPlayInfo> lastSongSearchResult;
    private ArrayList<PodcastInput> lastPodcastSearchResult;
    private ArrayList<Playlist> lastPlaylistSearchResult;
    private ArrayList<Album> lastAlbumSearchResult;
    private ArrayList<Artist> lastArtistSearchResult;
    private ArrayList<Host> lastHostSearchResult;

    // variable that indicates the type of the last search command
    // (1) - song; (2) - podcast; (3) - playlist (4) - album; (5) - artist; (6) - host
    private Integer lastSearchTypeIndicator;

    // true - something loaded in the player; false - nothing loaded in the player
    private Boolean loaded;

    // true - something has been selected; false - no selection has been made
    private Boolean selected;

    // true - a search command has been previously executed; false - no search has been executed
    private Boolean searched;

    // result of the last song selection
    private SongPlayInfo lastSelectedSong;

    // result of the last podcast selection
    private PodcastInput lastSelectedPodcast;

    // result of the last playlist selection
    private Playlist lastSelectedPlaylist;

    // result of the last album selection
    private Album lastSelectedAlbum;

    // variable that indicates the type of the last load command
    // (1) - song; (2) - podcast (3) - playlist; (4)- album
    private Integer lastLoadTypeIndicator;

    // results of the last load operation, according to the audio entity type
    private Playlist lastLoadedPlaylist;

    /* the same as the above, but these will be instances of some classes
    with more fields, containing more details */
    private SongPlayInfo lastLoadedSongPlayInfo;
    private PodcastPlayInfo lastLoadedPodcast;
    private Album lastLoadedAlbum;

    private ArrayList<PodcastPlayInfo> podcastPlayInfoList;

    private ArrayList<Playlist> playlists;

    private ArrayList<SongPlayInfo> likedSongs;
    private ArrayList<Playlist> following;

    // true - online; false - offline
    private Boolean connectionStatus;

    private ArrayList<Page> pageHistory;

    private int currentPageIndex;

    /* keep a list of all artists the user has ever listened to,
    associated with their number of listens */
    private ArrayList<WrappedArtist> wrappedArtists;

    private ArrayList<WrappedGenre> wrappedGenres;

    private ArrayList<WrappedAlbum> wrappedAlbums;

    private ArrayList<WrappedSong> wrappedSongs;

    private ArrayList<WrappedEpisode> wrappedEpisodes;

    // list of all the merch items bought by this user
    private ArrayList<Merch> boughtMerch;

    private Playlist fansPlaylistRecommendations;
    private ArrayList<String> songRecommendations;
    private SongPlayInfo lastSongRecommendation;
    private Playlist randomPlaylistRecommendation;
    private String lastRecommendationType;
    private ArrayList<ObjectNode> notifications;

    // true - premium; false - not premium
    private boolean isPremium;

    // list of songs that have been played by the user while being premium
    private ArrayList<WrappedSong> songsPlayedWhilePremium;

    private ArrayList<WrappedArtist> artistsPlayedWhilePremium;

    public NormalUser(final String username) {
        super("user", username);
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        podcastPlayInfoList = new ArrayList<>();
        lastLoadedSongPlayInfo = new SongPlayInfo();
        following = new ArrayList<>();
        loaded = false;
        selected = false;
        searched = false;
        connectionStatus = true;
        wrappedArtists = new ArrayList<>();
        wrappedAlbums = new ArrayList<>();
        wrappedGenres = new ArrayList<>();
        wrappedEpisodes = new ArrayList<>();
        wrappedSongs = new ArrayList<>();
        boughtMerch = new ArrayList<>();
        pageHistory = new ArrayList<>();

        // a new user is automatically on home page
        Page homePage = new HomePage(this);

        pageHistory.add(homePage);
        currentPageIndex = 0;
        songRecommendations = new ArrayList<>();
        notifications = new ArrayList<>();
        isPremium = false;
    }

    /**
     * Updates notifications for the user with a new notification.
     *
     * @param newNotification The new notification to be added.
     */
    public void updateNotifications(final ObjectNode newNotification) {
        notifications.add(newNotification);
    }

    /**
     * Resets the notifications for the user, clearing all existing notifications.
     */
    public void resetNotifications() {
        notifications.clear();
    }


    /**
     * Updates artists' song revenues for premium users when they stop being premium.
     */
    public void updateArtistsSongRevenues() {
        /* this will be used only for premium users to update the artists' song revenues
        when the users stop being premium */

        // get total number of songs listened by this user while being premium
        int totalNumberOfSongs = 0;
        for (WrappedSong song : songsPlayedWhilePremium) {
            totalNumberOfSongs += song.getListens();
        }

        for (Artist artist : Main.artistsList) {
            for (WrappedArtist wrappedArtist : artistsPlayedWhilePremium) {
                if (artist.getUsername().equals(wrappedArtist.getArtistName())) {
                    calculateMonetizationForThisArtist(artist, wrappedArtist, totalNumberOfSongs);
                    break;
                }
            }
        }
    }

    /**
     * Calculates and updates the monetization for a specific artist and their songs.
     *
     * @param artist              The artist for which monetization is calculated.
     * @param wrappedArtist       The wrapped artist information.
     * @param totalNumberOfSongs  Total number of songs listened to by the user while being premium.
     */
    public void calculateMonetizationForThisArtist(final Artist artist,
                                                   final WrappedArtist wrappedArtist,
                                                   final int totalNumberOfSongs) {
        // calculate monetization for this artist
        double monetization = (double) Math.pow(MONETIZATION_POWER_BASE,
                MONETIZATION_POWER_EXPONENT) * wrappedArtist.getListens() / totalNumberOfSongs;

        // update the artist's song revenue
        artist.setSongRevenue(artist.getSongRevenue() + monetization);

        // calculate monetization for each of the artist's songs
        for (WrappedSong song : getSongsPlayedWhilePremium()) {
            if (song.getSongPlayInfo().getSong().getArtist().equals(artist.getUsername())) {
                updateArtistMonetizedSongsList(artist, song, monetization, wrappedArtist);
            }
        }
    }

    /**
     * Updates the monetized songs list for a specific artist and song.
     *
     * @param artist              The artist for which the songs are monetized.
     * @param song                The song for which monetization is updated.
     * @param totalArtistMonetization  Total monetization for the artist's songs.
     * @param wrappedArtist       The wrapped artist information.
     */
    public void updateArtistMonetizedSongsList(final Artist artist, final WrappedSong song,
                                               final double totalArtistMonetization,
                                               final WrappedArtist wrappedArtist) {
        double songMonetization = totalArtistMonetization * song.getListens()
                / wrappedArtist.getListens();

        // update the song's monetization
        boolean found = false;
        for (MonetizedSong monetizedSong : artist.getMonetizedSongs()) {
            if (song.getSongPlayInfo().getSong().getName().equals(monetizedSong.
                    getSongPlayInfo().getSong().getName())) {
                monetizedSong.setRevenue(monetizedSong.getRevenue() + songMonetization);
                found = true;
                break;
            }
        }
        if (!found) {
            // if we are here, it means that the song has not been found, so we need to add it
            MonetizedSong newMonetizedSong = new MonetizedSong(song.getSongPlayInfo(),
                    songMonetization);
            artist.getMonetizedSongs().add(newMonetizedSong);
        }
    }

    /**
     * Updates the list of wrapped artists based on the artist name.
     *
     * @param artistName          The name of the artist to update.
     * @param wrappedArtistsList  The list of wrapped artists.
     */
    public void updateWrappedArtists(final String artistName,
                                     final ArrayList<WrappedArtist> wrappedArtistsList) {
        // look for the artist in the list of wrapped artists
        for (WrappedArtist wrappedArtist : wrappedArtistsList) {
            if (wrappedArtist.getArtistName().equals(artistName)) {
                // increment the number of listens
                wrappedArtist.setListens(wrappedArtist.getListens() + 1);
                return;
            }
        }

        /* if we are here, it means that the artist was not found,
        so we need to add it to the list */
        WrappedArtist newWrappedArtist = new WrappedArtist(artistName);
        wrappedArtistsList.add(newWrappedArtist);
    }

    /**
     * Updates the list of wrapped genres based on the genre.
     *
     * @param genre               The genre to update.
     * @param wrappedGenreList    The list of wrapped genres.
     */
    public void updateWrappedGenres(final String genre,
                                    final ArrayList<WrappedGenre> wrappedGenreList) {
        // look for the genre in the list of wrapped genres
        for (WrappedGenre wrappedGenre : wrappedGenreList) {
            if (wrappedGenre.getGenre().equals(genre)) {
                // increment the number of listens
                wrappedGenre.setListens(wrappedGenre.getListens() + 1);
                return;
            }
        }

        // if we are here, it means that the genre was not found, so we need to add it to the list
        WrappedGenre newWrappedGenre = new WrappedGenre(genre);
        wrappedGenreList.add(newWrappedGenre);
    }

    /**
     * Updates the list of wrapped albums based on the album name.
     *
     * @param albumName           The name of the album to update.
     */
    public void updateWrappedAlbums(final String albumName) {
        // look for the album in the list of wrapped albums
        for (WrappedAlbum wrappedAlbum : wrappedAlbums) {
            if (wrappedAlbum.getAlbumName().equals(albumName)) {
                // increment the number of listens
                wrappedAlbum.setListens(wrappedAlbum.getListens() + 1);
                return;
            }
        }

        // if we are here, it means that the genre was not found, so we need to add it to the list
        WrappedAlbum newWrappedAlbum = new WrappedAlbum(albumName);
        wrappedAlbums.add(newWrappedAlbum);
    }

    /**
     * Updates the list of wrapped songs based on the song play information.
     *
     * @param songPlayInfo        The song play information to update.
     * @param wrappedSongsList    The list of wrapped songs.
     */
    public void updateWrappedSongs(final SongPlayInfo songPlayInfo, final
    ArrayList<WrappedSong> wrappedSongsList) {
        // look for the song in the list of wrapped songs
        for (WrappedSong wrappedSong : wrappedSongsList) {
            if (wrappedSong.getSongPlayInfo().getSong().getName().
                    equals(songPlayInfo.getSong().getName())) {
                // increment the number of listens
                wrappedSong.setListens(wrappedSong.getListens() + 1);
                return;
            }
        }

        // if we are here, it means that the song was not found, so we need to add it to the list
        WrappedSong newWrappedSong = new WrappedSong(songPlayInfo);
        wrappedSongsList.add(newWrappedSong);
    }

    /**
     * Updates the list of wrapped episodes based on the episode information.
     *
     * @param episode             The episode information to update.
     */
    public void updateWrappedEpisodes(final EpisodeInput episode) {
        // look for the episode in the list of wrapped episodes
        for (WrappedEpisode wrappedEpisode : wrappedEpisodes) {
            if (wrappedEpisode.getEpisode().getName().equals(episode.getName())) {
                // increment the number of listens
                wrappedEpisode.setListens(wrappedEpisode.getListens() + 1);
                return;
            }
        }

        // if we are here, it means that the episode was not found, so we need to add it to the list
        WrappedEpisode newWrappedEpisode = new WrappedEpisode(episode);
        wrappedEpisodes.add(newWrappedEpisode);
    }

    /**
     * Updates various entities (songs, genres, artists, albums) for a specific song.
     *
     * @param songPlayInfo        The song play information to update.
     */
    public void updateEverythingForSong(final SongPlayInfo songPlayInfo) {
        updateWrappedSongs(songPlayInfo, wrappedSongs);
        updateWrappedGenres(songPlayInfo.getSong().getGenre(), wrappedGenres);
        updateWrappedArtists(songPlayInfo.getSong().getArtist(), wrappedArtists);
        updateWrappedAlbums(songPlayInfo.getSong().getAlbum());
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(songPlayInfo.getSong().getArtist())) {
                artist.updateArtistWrappedAlbums(songPlayInfo.getSong().getAlbum());
                break;
            }
        }
    }

    /**
     * Updates songs and artists for premium users based on the song play information.
     *
     * @param songPlayInfo        The song play information to update for premium users.
     */
    public void updateSongAndArtistForPremiumUser(final SongPlayInfo songPlayInfo) {
        updateWrappedSongs(songPlayInfo, songsPlayedWhilePremium);
        updateWrappedArtists(songPlayInfo.getSong().getArtist(), artistsPlayedWhilePremium);
    }

    /**
     * Accepts a deletion visitor and determines if the user can be deleted
     * based on the visitor's logic.
     *
     * @param visitor The deletion visitor.
     * @return True if the user can be deleted, false otherwise.
     */
    @Override
    public Boolean acceptDeletion(final VisitorDeletion visitor) {
        return visitor.canBeDeleted(this);
    }

    /**
     * Accepts a wrapped visitor and retrieves the wrapped result node.
     *
     * @param visitor The wrapped visitor.
     */
    @Override
    public void acceptWrapped(final VisitorWrapped visitor) {
        visitor.getWrappedResultNode(this);
    }

    /**
     * Checks if the last loaded song is still playing or has finished.
     *
     * @param timestamp The current timestamp.
     * @return True if the song is still loaded, false if it has finished.
     */
    public Boolean isSongStillLoaded(final Integer timestamp) {
        // update song status from the last update until now
        if (this.getLastLoadedSongPlayInfo() == null) {
            return false;
        }

        // check if the song was on play
        if (!this.getLastLoadedSongPlayInfo().getSongPaused()) {
            SongPlayInfo songPlayInfo = this.getLastLoadedSongPlayInfo();
            songPlayInfo.updateSongStatus(this, timestamp);
            if (this.getLastLoadedSongPlayInfo() == null) {
                // the song has finished => it isn't loaded anymore
                return false;
            }
            this.getLastLoadedSongPlayInfo().setLastPlayTimestamp(timestamp);
        }

        return true;
    }

    /**
     * Checks if the last loaded podcast is still playing or has finished.
     *
     * @param timestamp The current timestamp.
     * @return True if the podcast is still loaded, false if it has finished.
     */
    public Boolean isPodcastStillLoaded(final Integer timestamp) {
        // update podcast status from the last update until now
        if (this.getLastLoadedPodcast() == null) {
            return false;
        }

        // check if the loaded podcast is on play
        if (!this.getLastLoadedPodcast().getPodcastPaused()) {
            PodcastPlayInfo podcast = this.getLastLoadedPodcast();
            podcast.updatePodcastStatus(this, timestamp);
            // check if the podcast is null after leaving the previous method
            if (this.getLastLoadedPodcast() == null) {
                // the podcast has finished => it isn't loaded anymore
                return false;
            }
            this.getLastLoadedPodcast().setLastPlayTimestamp(timestamp);
            this.getLastLoadedPodcast().setPodcastPaused(false);
        }
        return true;
    }

    /**
     * Checks if the last loaded playlist is still playing or has finished.
     *
     * @param timestamp The current timestamp.
     * @return True if the playlist is still loaded, false if it has finished.
     */
    public Boolean isPlaylistStillLoaded(final Integer timestamp) {
        // update playlist status from the last update until now
        if (this.getLastLoadedPlaylist() == null) {
            return false;
        }

        // check if the loaded playlist was on play
        if (!this.getLastLoadedPlaylist().getPaused()) {
            Playlist playList = this.getLastLoadedPlaylist();

            // check playlist shuffle status
            if (!playList.getShuffleStatus()) {
                ArrayList<SongPlayInfo> playlistSongs;
                playlistSongs = this.getLastLoadedPlaylist().getPlaylistSongs();
                playList.updatePlaylistStatus(this, timestamp, playlistSongs);
            } else {
                ArrayList<SongPlayInfo> shuffledSongs;
                shuffledSongs = this.getLastLoadedPlaylist().getShuffledPlaylistSongs();
                playList.updatePlaylistStatus(this, timestamp, shuffledSongs);
            }

            if (this.getLastLoadedPlaylist() == null) {
                // the playlist has finished => it isn't loaded anymore
                return false;
            }
            this.getLastLoadedPlaylist().setLastPlayTimestamp(timestamp);
        }
        return true;
    }

    /**
     * Checks if the last loaded album is still playing or has finished.
     *
     * @param timestamp The current timestamp.
     * @return True if the album is still loaded, false if it has finished.
     */
    public Boolean isAlbumStillLoaded(final Integer timestamp) {
        // update album status from the last update until now
        if (this.getLastLoadedAlbum() == null) {
            return false;
        }

        // check if the loaded album was on play
        if (!this.getLastLoadedAlbum().getPaused()) {
            Album album = this.getLastLoadedAlbum();

            // check album shuffle status
            if (!album.getShuffleStatus()) {
                ArrayList<SongPlayInfo> albumSongs;
                albumSongs = this.getLastLoadedAlbum().getSongs();
                album.updateAlbumStatus(this, timestamp, albumSongs);
            } else {
                album.updateAlbumStatus(this, timestamp, album.getShuffledSongs());
            }

            if (this.getLastLoadedAlbum() == null) {
                // the album has finished => it isn't loaded anymore
                return false;
            }
            this.getLastLoadedAlbum().setLastPlayTimestamp(timestamp);
        }
        return true;
    }

    /**
     * Checks if the last loaded audio entity is still loaded or has finished.
     *
     * @param timestamp The current timestamp.
     * @return True if the audio entity is still loaded, false if it has finished.
     */
    public Boolean stillHasSomethingLoaded(final Integer timestamp) {
        if (!this.getLoaded()) {
            return false;
        }

        switch (this.getLastLoadTypeIndicator()) {
            case TYPE_SONG:
                return isSongStillLoaded(timestamp);
            case TYPE_PODCAST:
                return isPodcastStillLoaded(timestamp);
            case TYPE_PLAYLIST:
                return isPlaylistStillLoaded(timestamp);
            case TYPE_ALBUM:
                return isAlbumStillLoaded(timestamp);
            default:
                return false;
        }
    }


    /**
     * Gets the list of liked songs for the user.
     *
     * @return The list of liked songs.
     */
    public ArrayList<SongPlayInfo> getLikedSongs() {
        return likedSongs;
    }

    /**
     * Sets the list of liked songs for the user.
     *
     * @param likedSongs The new list of liked songs.
     */
    public void setLikedSongs(final ArrayList<SongPlayInfo> likedSongs) {
        this.likedSongs = likedSongs;
    }

    /**
     * Gets the list of playlists created by the user.
     *
     * @return The list of user playlists.
     */
    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    /**
     * Sets the list of playlists for the user.
     *
     * @param playlists The new list of user playlists.
     */
    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    /**
     * Gets the last search result for songs.
     *
     * @return The list of the last searched songs.
     */
    public ArrayList<SongPlayInfo> getLastSongSearchResult() {
        return lastSongSearchResult;
    }

    /**
     * Sets the last search result for songs.
     *
     * @param lastSongSearchResult The new list of the last searched songs.
     */
    public void setLastSongSearchResult(final ArrayList<SongPlayInfo> lastSongSearchResult) {
        this.lastSongSearchResult = lastSongSearchResult;
    }

    /**
     * Gets the last search result for podcasts.
     *
     * @return The list of the last searched podcasts.
     */
    public ArrayList<PodcastInput> getLastPodcastSearchResult() {
        return lastPodcastSearchResult;
    }

    /**
     * Sets the last search result for podcasts.
     *
     * @param lastPodcastSearchResult The new list of the last searched podcasts.
     */
    public void setLastPodcastSearchResult(final ArrayList<PodcastInput> lastPodcastSearchResult) {
        this.lastPodcastSearchResult = lastPodcastSearchResult;
    }

    /**
     * Gets the type of the last search command performed by the user.
     *
     * @return The type of the last search command (1 for song, 2 for podcast, 3 for playlist).
     */
    public Integer getLastSearchTypeIndicator() {
        return lastSearchTypeIndicator;
    }

    /**
     * Sets the type of the last search command performed by the user.
     *
     * @param lastSearchTypeIndicator The new type of the last search command.
     */
    public void setLastSearchTypeIndicator(final Integer lastSearchTypeIndicator) {
        this.lastSearchTypeIndicator = lastSearchTypeIndicator;
    }

    /**
     * Gets the last selected song by the user.
     *
     * @return The last selected song.
     */
    public SongPlayInfo getLastSelectedSong() {
        return lastSelectedSong;
    }

    /**
     * Sets the last selected song by the user.
     *
     * @param lastSelectedSong The new last selected song.
     */
    public void setLastSelectedSong(final SongPlayInfo lastSelectedSong) {
        this.lastSelectedSong = lastSelectedSong;
    }

    /**
     * Gets the last selected podcast by the user.
     *
     * @return The last selected podcast.
     */
    public PodcastInput getLastSelectedPodcast() {
        return lastSelectedPodcast;
    }

    /**
     * Sets the last selected podcast by the user.
     *
     * @param lastSelectedPodcast The new last selected podcast.
     */
    public void setLastSelectedPodcast(final PodcastInput lastSelectedPodcast) {
        this.lastSelectedPodcast = lastSelectedPodcast;
    }

    /**
     * Gets the type of the last load command performed by the user.
     *
     * @return The type of the last load command (1 for song, 2 for podcast, 3 for playlist).
     */
    public Integer getLastLoadTypeIndicator() {
        return lastLoadTypeIndicator;
    }

    /**
     * Sets the type of the last load command performed by the user.
     *
     * @param lastLoadTypeIndicator The new type of the last load command.
     */
    public void setLastLoadTypeIndicator(final Integer lastLoadTypeIndicator) {
        this.lastLoadTypeIndicator = lastLoadTypeIndicator;
    }

    /**
     * Gets the last search result for playlists.
     *
     * @return The list of the last searched playlists.
     */
    public ArrayList<Playlist> getLastPlaylistSearchResult() {
        return lastPlaylistSearchResult;
    }

    /**
     * Sets the last search result for playlists.
     *
     * @param lastPlaylistSearchResult The new list of the last searched playlists.
     */
    public void setLastPlaylistSearchResult(final ArrayList<Playlist> lastPlaylistSearchResult) {
        this.lastPlaylistSearchResult = lastPlaylistSearchResult;
    }

    /**
     * Gets the last selected playlist by the user.
     *
     * @return The last selected playlist.
     */
    public Playlist getLastSelectedPlaylist() {
        return lastSelectedPlaylist;
    }

    /**
     * Sets the last selected playlist by the user.
     *
     * @param lastSelectedPlaylist The new last selected playlist.
     */
    public void setLastSelectedPlaylist(final Playlist lastSelectedPlaylist) {
        this.lastSelectedPlaylist = lastSelectedPlaylist;
    }

    /**
     * Gets the list of podcast play information.
     *
     * @return The list of podcast play information.
     */
    public ArrayList<PodcastPlayInfo> getPodcastPlayInfoList() {
        return podcastPlayInfoList;
    }

    /**
     * Sets the list of podcast play information.
     *
     * @param podcastPlayInfoList The new list of podcast play information.
     */
    public void setPodcastPlayInfoList(final ArrayList<PodcastPlayInfo> podcastPlayInfoList) {
        this.podcastPlayInfoList = podcastPlayInfoList;
    }

    /**
     * Gets the last loaded podcast by the user.
     *
     * @return The last loaded podcast.
     */
    public PodcastPlayInfo getLastLoadedPodcast() {
        return lastLoadedPodcast;
    }

    /**
     * Sets the last loaded podcast by the user.
     *
     * @param lastLoadedPodcast The new last loaded podcast.
     */
    public void setLastLoadedPodcast(final PodcastPlayInfo lastLoadedPodcast) {
        this.lastLoadedPodcast = lastLoadedPodcast;
    }

    /**
     * Gets the last loaded playlist by the user.
     *
     * @return The last loaded playlist.
     */
    public Playlist getLastLoadedPlaylist() {
        return lastLoadedPlaylist;
    }

    /**
     * Sets the last loaded playlist by the user.
     *
     * @param lastLoadedPlaylist The new last loaded playlist.
     */
    public void setLastLoadedPlaylist(final Playlist lastLoadedPlaylist) {
        this.lastLoadedPlaylist = lastLoadedPlaylist;
    }

    /**
     * Gets the last loaded song play information by the user.
     *
     * @return The last loaded song play information.
     */
    public SongPlayInfo getLastLoadedSongPlayInfo() {
        return lastLoadedSongPlayInfo;
    }

    /**
     * Sets the last loaded song play information by the user.
     *
     * @param lastLoadedSongPlayInfo The new last loaded song play information.
     */
    public void setLastLoadedSongPlayInfo(final SongPlayInfo lastLoadedSongPlayInfo) {
        this.lastLoadedSongPlayInfo = lastLoadedSongPlayInfo;
    }

    /**
     * Gets the list of playlists the user is following.
     *
     * @return The list of playlists the user is following.
     */
    public ArrayList<Playlist> getFollowing() {
        return following;
    }

    /**
     * Sets the list of playlists the user is following.
     *
     * @param following The new list of playlists the user is following.
     */
    public void setFollowing(final ArrayList<Playlist> following) {
        this.following = following;
    }

    /**
     * Gets the loaded status of the user in the music player.
     *
     * @return True if something is loaded, false if nothing is loaded.
     */
    public Boolean getLoaded() {
        return loaded;
    }

    /**
     * Sets the loaded status of the user in the music player.
     *
     * @param loaded The new loaded status.
     */
    public void setLoaded(final Boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Gets the selected status of the user in the music player.
     *
     * @return True if something is selected, false if nothing is selected.
     */
    public Boolean getSelected() {
        return selected;
    }

    /**
     * Sets the selected status of the user in the music player.
     *
     * @param selected The new selected status.
     */
    public void setSelected(final Boolean selected) {
        this.selected = selected;
    }

    /**
     * Gets the searched status of the user in the music player.
     *
     * @return True if a search has been executed, false otherwise.
     */
    public Boolean getSearched() {
        return searched;
    }

    /**
     * Sets the searched status of the user in the music player.
     *
     * @param searched The new searched status.
     */
    public void setSearched(final Boolean searched) {
        this.searched = searched;
    }

    /**
     * Gets the connection status of the user.
     *
     * @return True if the user is online, false if the user is offline.
     */
    public Boolean getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Sets the connection status of the user.
     *
     * @param connectionStatus The new connection status.
     */
    public void setConnectionStatus(final Boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<Album> getLastAlbumSearchResult() {
        return lastAlbumSearchResult;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param lastAlbumSearchResult The new list of the last searched artists.
     */
    public void setLastAlbumSearchResult(final ArrayList<Album> lastAlbumSearchResult) {
        this.lastAlbumSearchResult = lastAlbumSearchResult;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<Artist> getLastArtistSearchResult() {
        return lastArtistSearchResult;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param lastArtistSearchResult The new list of the last searched artists.
     */
    public void setLastArtistSearchResult(final ArrayList<Artist> lastArtistSearchResult) {
        this.lastArtistSearchResult = lastArtistSearchResult;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public Album getLastSelectedAlbum() {
        return lastSelectedAlbum;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param lastSelectedAlbum The new list of the last searched artists.
     */
    public void setLastSelectedAlbum(final Album lastSelectedAlbum) {
        this.lastSelectedAlbum = lastSelectedAlbum;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public Album getLastLoadedAlbum() {
        return lastLoadedAlbum;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param lastLoadedAlbum The new list of the last searched artists.
     */
    public void setLastLoadedAlbum(final Album lastLoadedAlbum) {
        this.lastLoadedAlbum = lastLoadedAlbum;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<Host> getLastHostSearchResult() {
        return lastHostSearchResult;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param lastHostSearchResult The new list of the last searched artists.
     */
    public void setLastHostSearchResult(final ArrayList<Host> lastHostSearchResult) {
        this.lastHostSearchResult = lastHostSearchResult;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<WrappedArtist> getWrappedArtists() {
        return wrappedArtists;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param wrappedArtists The new list of the last searched artists.
     */
    public void setWrappedArtists(final ArrayList<WrappedArtist> wrappedArtists) {
        this.wrappedArtists = wrappedArtists;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<WrappedGenre> getWrappedGenres() {
        return wrappedGenres;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param wrappedGenres The new list of the last searched artists.
     */
    public void setWrappedGenres(final ArrayList<WrappedGenre> wrappedGenres) {
        this.wrappedGenres = wrappedGenres;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<WrappedAlbum> getWrappedAlbums() {
        return wrappedAlbums;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param wrappedAlbums The new list of the last searched artists.
     */
    public void setWrappedAlbums(final ArrayList<WrappedAlbum> wrappedAlbums) {
        this.wrappedAlbums = wrappedAlbums;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<WrappedSong> getWrappedSongs() {
        return wrappedSongs;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param wrappedSongs The new list of the last searched artists.
     */
    public void setWrappedSongs(final ArrayList<WrappedSong> wrappedSongs) {
        this.wrappedSongs = wrappedSongs;
    }

    /**
     * Gets the last search result for artists.
     *
     * @return The list of the last searched artists.
     */
    public ArrayList<WrappedEpisode> getWrappedEpisodes() {
        return wrappedEpisodes;
    }

    /**
     * Sets the last search result for artists.
     *
     * @param wrappedEpisodes The new list of the last searched artists.
     */
    public void setWrappedEpisodes(final ArrayList<WrappedEpisode> wrappedEpisodes) {
        this.wrappedEpisodes = wrappedEpisodes;
    }

    /**
     * Gets the list of merch items bought by the user.
     *
     * @return The list of merch items bought by the user.
     */
    public ArrayList<Merch> getBoughtMerch() {
        return boughtMerch;
    }

    /**
     * Sets the list of merch items bought by the user.
     *
     * @param boughtMerch The new list of merch items bought by the user.
     */
    public void setBoughtMerch(final ArrayList<Merch> boughtMerch) {
        this.boughtMerch = boughtMerch;
    }

    /**
     * Gets the list of pages visited by the user.
     *
     * @return The list of pages visited by the user.
     */
    public ArrayList<Page> getPageHistory() {
        return pageHistory;
    }

    /**
     * Sets the list of pages visited by the user.
     *
     * @param pageHistory The new list of pages visited by the user.
     */
    public void setPageHistory(final ArrayList<Page> pageHistory) {
        this.pageHistory = pageHistory;
    }

    /**
     * Gets the index of the current page in the page history.
     *
     * @return The index of the current page in the page history.
     */
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * Sets the index of the current page in the page history.
     *
     * @param currentPageIndex The new index of the current page in the page history.
     */
    public void setCurrentPageIndex(final int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    /**
     * Gets the playlist recommendations for the user.
     *
     * @return The playlist recommendations for the user.
     */
    public Playlist getFansPlaylistRecommendations() {
        return fansPlaylistRecommendations;
    }

    /**
     * Sets the playlist recommendations for the user.
     *
     * @param fansPlaylistRecommendations The new playlist recommendations for the user.
     */
    public void setFansPlaylistRecommendations(final Playlist fansPlaylistRecommendations) {
        this.fansPlaylistRecommendations = fansPlaylistRecommendations;
    }

    /**
     * Gets the song recommendations for the user.
     *
     * @return The song recommendations for the user.
     */
    public ArrayList<String> getSongRecommendations() {
        return songRecommendations;
    }

    /**
     * Sets the song recommendations for the user.
     *
     * @param songRecommendations The new song recommendations for the user.
     */
    public void setSongRecommendations(final ArrayList<String> songRecommendations) {
        this.songRecommendations = songRecommendations;
    }

    /**
     * Gets the last song recommendation for the user.
     *
     * @return The last song recommendation for the user.
     */
    public Playlist getRandomPlaylistRecommendation() {
        return randomPlaylistRecommendation;
    }

    /**
     * Sets the last song recommendation for the user.
     *
     * @param randomPlaylistRecommendation The new last song recommendation for the user.
     */
    public void setRandomPlaylistRecommendation(final Playlist randomPlaylistRecommendation) {
        this.randomPlaylistRecommendation = randomPlaylistRecommendation;
    }

    /**
     * Gets the last song recommendation for the user.
     *
     * @return The last song recommendation for the user.
     */
    public SongPlayInfo getLastSongRecommendation() {
        return lastSongRecommendation;
    }

    /**
     * Sets the last song recommendation for the user.
     *
     * @param lastSongRecommendation The new last song recommendation for the user.
     */
    public void setLastSongRecommendation(final SongPlayInfo lastSongRecommendation) {
        this.lastSongRecommendation = lastSongRecommendation;
    }

    /**
     * Gets the last recommendation type for the user.
     *
     * @return The last recommendation type for the user.
     */
    public String getLastRecommendationType() {
        return lastRecommendationType;
    }

    /**
     * Sets the last recommendation type for the user.
     *
     * @param lastRecommendationType The new last recommendation type for the user.
     */
    public void setLastRecommendationType(final String lastRecommendationType) {
        this.lastRecommendationType = lastRecommendationType;
    }

    /**
     * Gets the last recommendation type for the user.
     *
     * @return The last recommendation type for the user.
     */
    public ArrayList<ObjectNode> getNotifications() {
        return notifications;
    }

    /**
     * Sets the last recommendation type for the user.
     *
     * @param notifications The new last recommendation type for the user.
     */
    public void setNotifications(final ArrayList<ObjectNode> notifications) {
        this.notifications = notifications;
    }

    /**
     * Gets the last recommendation type for the user.
     *
     * @return The last recommendation type for the user.
     */
    public boolean getIsPremium() {
        return isPremium;
    }

    /**
     * Sets the last recommendation type for the user.
     *
     * @param premium The new last recommendation type for the user.
     */
    public void setIsPremium(final boolean premium) {
        isPremium = premium;
    }

    /**
     * Gets the last recommendation type for the user.
     *
     * @return The last recommendation type for the user.
     */
    public ArrayList<WrappedSong> getSongsPlayedWhilePremium() {
        return songsPlayedWhilePremium;
    }

    /**
     * Sets the last recommendation type for the user.
     *
     * @param songsPlayedWhilePremium The new last recommendation type for the user.
     */
    public void setSongsPlayedWhilePremium(final ArrayList<WrappedSong> songsPlayedWhilePremium) {
        this.songsPlayedWhilePremium = songsPlayedWhilePremium;
    }

    /**
     * Gets the last recommendation type for the user.
     *
     * @return The last recommendation type for the user.
     */
    public ArrayList<WrappedArtist> getArtistsPlayedWhilePremium() {
        return artistsPlayedWhilePremium;
    }

    /**
     * Sets the last recommendation type for the user.
     *
     * @param artistsPlayedWhilePremium The new last
     *                                  recommendation type for the user.
     */
    public void setArtistsPlayedWhilePremium(final ArrayList<WrappedArtist>
                                                     artistsPlayedWhilePremium) {
        this.artistsPlayedWhilePremium = artistsPlayedWhilePremium;
    }
}
