package users;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.entitiesForArtist.Event;
import fileio.input.entitiesForArtist.Merch;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.Playlist;
import main.Main;
import main.adminCommands.VisitableDeletion;
import main.adminCommands.VisitorDeletion;
import main.statisticsCommands.VisitableWrapped;
import main.statisticsCommands.VisitorWrapped;
import fileio.input.wrappedEntities.MonetizedSong;
import fileio.input.wrappedEntities.WrappedAlbum;

import java.util.ArrayList;

public class Artist extends GeneralUser implements VisitableDeletion, VisitableWrapped, Subject {
    private static final int TYPE_ALBUM = 4;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_SONG = 1;
    private static final double HUNDRED = 100.0;
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merchItems;

    private double songRevenue;
    private double merchRevenue;
    private Integer ranking;
    private SongPlayInfo mostProfitableSong;

    // total number of likes from all songs in all albums
    private Integer numberOfLikes;

    // variable that indicates if the artist has ever had anything on play
    private boolean hadSomethingOnPlay;

    private ArrayList<WrappedAlbum> wrappedAlbums;
    private ArrayList<NormalUser> subscribers;

    private ArrayList<MonetizedSong> monetizedSongs;

    public Artist(final String type, final String username, final Integer age, final String city) {
        super(type, username, age, city);
        this.albums = new ArrayList<>();
        this.events = new ArrayList<>();
        this.merchItems = new ArrayList<>();
        this.numberOfLikes = 0;
        this.songRevenue = Math.round(0 * HUNDRED) / HUNDRED;
        this.merchRevenue = Math.round(0 * HUNDRED) / HUNDRED;
        this.ranking = 0;
        this.mostProfitableSong = null;
        this.hadSomethingOnPlay = false;
        this.wrappedAlbums = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        this.monetizedSongs = new ArrayList<>();
    }

    /**
     * Adds a NormalUser observer to the list of subscribers.
     *
     * @param observer The observer to be added.
     */
    public void addObserver(final NormalUser observer) {
        subscribers.add(observer);
    }

    /**
     * Removes a NormalUser observer from the list of subscribers.
     *
     * @param observer The observer to be removed.
     */
    public void removeObserver(final NormalUser observer) {
        subscribers.remove(observer);
    }

    /**
     * Notifies all subscribed observers with a new notification.
     *
     * @param newNotification The new notification to be sent to the observers.
     */
    public void notifyObservers(final ObjectNode newNotification) {
        for (NormalUser observer : subscribers) {
            observer.updateNotifications(newNotification);
        }
    }

    /**
     * Updates the number of listens for the artist's wrapped albums based
     * on the provided album name.
     *
     * @param albumName The name of the album to be updated.
     */
    public void updateArtistWrappedAlbums(final String albumName) {
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
     * Updates the total number of likes for the artist based on the likes of albums.
     */
    public void updateArtistNumberOfLikes() {
        Integer artistNumberOfLikes = 0;
        for (Album album : this.albums) {
            album.updateAlbumNumberOfLikes();
            artistNumberOfLikes += album.getNumberOfLikes();
        }
        this.setNumberOfLikes(artistNumberOfLikes);
    }

    /**
     * Accepts a visitor for deletion and checks if the artist can be deleted.
     *
     * @param visitor the deletion visitor.
     * @return true if the artist can be deleted, false otherwise.
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
     * Checks if the album to be deleted is loaded by any user.
     *
     * @param albumToBeDeleted the album to be deleted.
     * @param user             the user to check.
     * @return true if the album is not loaded, false otherwise.
     */
    // check if the user has loaded the album that needs to be deleted
    public Boolean checkLoadedAlbums(final Album albumToBeDeleted, final NormalUser user) {
        // check if the album to be deleted is the one that is loaded
        if (user.getLastLoadedAlbum().getName().equals(albumToBeDeleted.getName())) {
            return false;
        }
        return true;
    }

    /**
     * Checks if any song from the album to be deleted is loaded by any user.
     *
     * @param albumToBeDeleted the album to be deleted.
     * @param user             the user to check.
     * @return true if no song from the album is loaded, false otherwise.
     */
    public Boolean checkLoadedSongs(final Album albumToBeDeleted, final NormalUser user) {
        // check if the song in the user's player belongs to the album that needs to be deleted
        for (SongPlayInfo song : albumToBeDeleted.getSongs()) {
            if (user.getLastLoadedSongPlayInfo().getSong()
                    .getName().equals(song.getSong().getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any playlist containing a song from the album to be deleted is loaded by any user.
     *
     * @param albumToBeDeleted the album to be deleted.
     * @param user             the user to check.
     * @return true if no playlist containing a song from the album is loaded, false otherwise.
     */
    public Boolean checkLoadedPlaylists(final Album albumToBeDeleted, final NormalUser user) {
        /* check if any of the songs in the loaded playlist belongs
        to the album that needs to be deleted */
        for (SongPlayInfo song : albumToBeDeleted.getSongs()) {
            for (SongPlayInfo playlistSong : user.getLastLoadedPlaylist().getPlaylistSongs()) {
                if (playlistSong.getSong().getName().equals(song.getSong().getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the artist can delete the album based on user interactions.
     *
     * @param albumToBeDeleted the album to be deleted.
     * @param timestamp        the timestamp of the deletion command.
     * @return true if the artist can delete the album, false otherwise.
     */
    public Boolean canDeleteAlbum(final Album albumToBeDeleted, final Integer timestamp) {
        for (NormalUser user : Main.normalUserList) {
            // check if the user has anything loaded in player
            if (!user.stillHasSomethingLoaded(timestamp)) {
                continue;
            }

            // check if the user has the album loaded
            if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
                if (checkLoadedAlbums(albumToBeDeleted, user)) {
                    continue;
                } else {
                    return false;
                }
            }

            // check if the user has loaded a song
            if (user.getLastLoadTypeIndicator() == TYPE_SONG) {
                if (checkLoadedSongs(albumToBeDeleted, user)) {
                    continue;
                } else {
                    return false;
                }
            }

            // check if the user has loaded a playlist
            if (user.getLastLoadTypeIndicator() == TYPE_PLAYLIST) {
                if (checkLoadedPlaylists(albumToBeDeleted, user)) {
                    continue;
                } else {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Prepares for the removal of an album by removing its songs from various lists.
     *
     * @param albumToBeDeleted the album to be deleted.
     */
    public void prepareAlbumRemoval(final Album albumToBeDeleted) {
        // remove all album's songs from the big song list
        for (SongPlayInfo songPlayInfo : albumToBeDeleted.getSongs()) {
            // remove song from the big list of songs
            Main.songsList.remove(songPlayInfo);

            for (NormalUser user : Main.normalUserList) {
                removeSongFromEverywhere(user, songPlayInfo);
            }
        }

        // check if any user had the album selected => delete selection
        for (NormalUser user : Main.normalUserList) {
            if (!user.getSearched()) {
                continue;
            }
            if (user.getLastSearchTypeIndicator() == TYPE_ALBUM && user.getSelected()) {
                if (user.getLastSelectedAlbum().getName().equals(albumToBeDeleted.getName())) {
                    user.setLastSelectedAlbum(null);
                    user.setSelected(false);
                }
            }
        }
    }

    /**
     * Removes a song from various locations for a specific user, including playlists,
     * liked songs list, and the user's selected song if applicable.
     *
     * @param user           The user from whose lists the song will be removed.
     * @param songPlayInfo   The song play information to be removed.
     */
    public void removeSongFromEverywhere(final NormalUser user, final SongPlayInfo songPlayInfo) {
        // remove song from playlists
        removeSongFromPlaylists(user, songPlayInfo);

        // remove song from user's liked songs list
        removeSongFromLikedSongs(user, songPlayInfo);

        // if an user had selected a song from the album to be deleted, delete selection
        if (!user.getSearched()) {
            return;
        }
        removeSongSelection(user, songPlayInfo);
    }


    /**
     * Removes a song from the playlists of a specific user.
     *
     * @param user           The user from whose playlists the song will be removed.
     * @param songPlayInfo   The song play information to be removed.
     */
    public void removeSongFromPlaylists(final NormalUser user, final SongPlayInfo songPlayInfo) {
        for (Playlist playlist : user.getPlaylists()) {
            for (SongPlayInfo playlistSong : playlist.getPlaylistSongs()) {
                if (playlistSong.getSong().getName().equals(songPlayInfo
                        .getSong().getName())) {
                    playlist.getPlaylistSongs().remove(playlistSong);

                    // also look for the song in the shuffled songs list
                    if (playlist.getShuffledPlaylistSongs() != null) {
                        playlist.getShuffledPlaylistSongs().remove(playlistSong);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Removes a song selection for a specific user.
     *
     * @param user           The user for whom the song selection will be removed.
     * @param songPlayInfo   The song play information for the selected song.
     */
    public void removeSongSelection(final NormalUser user, final SongPlayInfo songPlayInfo) {
        if (user.getLastSearchTypeIndicator() == TYPE_SONG && user.getSelected()) {
            if (user.getLastSelectedSong().getSong().getName()
                    .equals(songPlayInfo.getSong().getName())) {
                user.setLastSelectedSong(null);
                user.setSelected(false);
            }
        }
    }

    /**
     * Removes a song from the liked songs list of a specific user.
     *
     * @param user           The user from whose liked songs the song will be removed.
     * @param songPlayInfo   The song play information for the liked song.
     */
    public void removeSongFromLikedSongs(final NormalUser user, final SongPlayInfo songPlayInfo) {
        for (SongPlayInfo likedSong : user.getLikedSongs()) {
            if (likedSong.getSong().getName().equals(songPlayInfo.getSong().getName())) {
                user.getLikedSongs().remove(likedSong);
                break;
            }
        }
    }

    /**
     * Gets the list of albums associated with the artist.
     *
     * @return the list of albums.
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     * Sets the list of albums associated with the artist.
     *
     * @param albums the list of albums to be set.
     */
    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    /**
     * Gets the list of events associated with the artist.
     *
     * @return the list of events.
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of events associated with the artist.
     *
     * @param events the list of events to be set.
     */
    public void setEvents(final ArrayList<Event> events) {
        this.events = events;
    }

    /**
     * Gets the list of merch items associated with the artist.
     *
     * @return the list of merch items.
     */
    public ArrayList<Merch> getMerchItems() {
        return merchItems;
    }

    /**
     * Sets the list of merch items associated with the artist.
     *
     * @param merchItems the list of merch items to be set.
     */
    public void setMerchItems(final ArrayList<Merch> merchItems) {
        this.merchItems = merchItems;
    }

    /**
     * Gets the total number of likes for the artist.
     *
     * @return the total number of likes.
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * Sets the total number of likes for the artist.
     *
     * @param numberOfLikes the total number of likes to be set.
     */
    public void setNumberOfLikes(final Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    /**
     * Gets the total number of plays for the artist.
     *
     * @return the total number of plays.
     */
    public double getSongRevenue() {
        return songRevenue;
    }

    /**
     * Sets the total number of plays for the artist.
     *
     * @param songRevenue the total number of plays to be set.
     */
    public void setSongRevenue(final Integer songRevenue) {
        this.songRevenue = songRevenue;
    }

    /**
     * Gets the total number of plays for the artist.
     *
     * @return the total number of plays.
     */
    public double getMerchRevenue() {
        return merchRevenue;
    }

    /**
     * Sets the total number of plays for the artist.
     *
     * @param merchRevenue the total number of plays to be set.
     */
    public void setMerchRevenue(final double merchRevenue) {
        this.merchRevenue = merchRevenue;
    }

    /**
     * Gets the ranking of the artist.
     *
     * @return the ranking.
     */
    public Integer getRanking() {
        return ranking;
    }

    /**
     * Sets the ranking of the artist.
     *
     * @param ranking the ranking to be set.
     */
    public void setRanking(final Integer ranking) {
        this.ranking = ranking;
    }

    /**
     * Gets the most profitable song of the artist.
     *
     * @return the most profitable song.
     */
    public SongPlayInfo getMostProfitableSong() {
        return mostProfitableSong;
    }

    /**
     * Sets the most profitable song of the artist.
     *
     * @param mostProfitableSong the most profitable song to be set.
     */
    public void setMostProfitableSong(final SongPlayInfo mostProfitableSong) {
        this.mostProfitableSong = mostProfitableSong;
    }

    /**
     * Gets the variable that indicates if the artist has ever had anything on play.
     *
     * @return the variable that indicates if the artist has ever had anything on play.
     */
    public boolean getHadSomethingOnPlay() {
        return hadSomethingOnPlay;
    }

    /**
     * Sets the variable that indicates if the artist has ever had anything on play.
     *
     * @param hadSomethingOnPlay the variable to be set.
     */
    public void setHadSomethingOnPlay(final boolean hadSomethingOnPlay) {
        this.hadSomethingOnPlay = hadSomethingOnPlay;
    }

    /**
     * Gets the list of wrapped albums associated with the artist.
     *
     * @return the list of wrapped albums.
     */
    public void setSongRevenue(final double songRevenue) {
        this.songRevenue = songRevenue;
    }

    /**
     * Gets the list of wrapped albums associated with the artist.
     *
     * @return the list of wrapped albums.
     */
    public ArrayList<WrappedAlbum> getWrappedAlbums() {
        return wrappedAlbums;
    }

    /**
     * Sets the list of wrapped albums associated with the artist.
     *
     * @param wrappedAlbums the list of wrapped albums to be set.
     */
    public void setWrappedAlbums(final ArrayList<WrappedAlbum> wrappedAlbums) {
        this.wrappedAlbums = wrappedAlbums;
    }

    /**
     * Gets the list of subscribers associated with the artist.
     *
     * @return the list of subscribers.
     */
    public ArrayList<NormalUser> getSubscribers() {
        return subscribers;
    }

    /**
     * Sets the list of subscribers associated with the artist.
     *
     * @param subscribers the list of subscribers to be set.
     */
    public void setSubscribers(final ArrayList<NormalUser> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Gets the list of monetized songs associated with the artist.
     *
     * @return the list of monetized songs.
     */
    public ArrayList<MonetizedSong> getMonetizedSongs() {
        return monetizedSongs;
    }

    /**
     * Sets the list of monetized songs associated with the artist.
     *
     * @param monetizedSongs the list of monetized songs to be set.
     */
    public void setMonetizedSongs(final ArrayList<MonetizedSong> monetizedSongs) {
        this.monetizedSongs = monetizedSongs;
    }
}
