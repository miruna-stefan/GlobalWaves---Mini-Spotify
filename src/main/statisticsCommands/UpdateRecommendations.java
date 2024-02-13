package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;
import fileio.input.wrappedEntities.WrappedFan;
import fileio.input.wrappedEntities.WrappedGenre;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

public final class UpdateRecommendations extends StandardStatisticsCommand {
    private String username;
    private String recommendationType;
    private static final int MAX_RESULTS = 3;

    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;
    private static final int MINIMUM_PASSED_SECONDS = 30;
    private static final int NUMBER_OF_RANKED_GENRES = 3;
    private static final int NUMBER_OF_RANKED_SONGS = 5;

    // Singleton instance field
    private static UpdateRecommendations instance = null;

    /**
     * Constructs a new UpdateRecommendations with the specified parameters.
     *
     * @param username           the username associated with the command.
     * @param timestamp          the timestamp of the command.
     * @param recommendationType the type of recommendation to update.
     * @param node               the JSON node associated with the command.
     */
    private UpdateRecommendations(final String command, final String username,
                                  final Integer timestamp,
                                  final String recommendationType, final ObjectNode node) {
        super(command, timestamp, node);
        this.username = username;
        this.recommendationType = recommendationType;
    }

    /**
     * Gets the singleton instance of UpdateRecommendations.
     *
     * @param command           the command string.
     * @param username          the username associated with the command.
     * @param timestamp         the timestamp of the command.
     * @param recommendationType the type of recommendation to update.
     * @param node              the JSON node associated with the command.
     * @return the singleton instance of UpdateRecommendations.
     */
    public static UpdateRecommendations getInstance(final String command, final String username,
                                                    final Integer timestamp,
                                                    final String recommendationType,
                                                    final ObjectNode node) {
        if (instance == null) {
            instance = new UpdateRecommendations(command, username,
                    timestamp, recommendationType, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setRecommendationType(recommendationType);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Gets a random song recommendation for the user.
     *
     * @param user the normal user.
     * @return a message indicating the success of the operation.
     */
    public String getRandomSongRecommendation(final NormalUser user) {
        /* identify the current song that is playing for the user and
        calculate how mush time has passed since it started */
        SongPlayInfo currentSongPlayInfo = null;
        int passedTime = 0;
        switch (user.getLastLoadTypeIndicator()) {
            case TYPE_SONG:
                currentSongPlayInfo = user.getLastLoadedSongPlayInfo();
                passedTime = user.getLastLoadedSongPlayInfo().getCurrentSecond();
                break;
            case TYPE_PLAYLIST:
                if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                    currentSongPlayInfo = user.getLastLoadedPlaylist().getPlaylistSongs().
                            get(user.getLastLoadedPlaylist().getCurrentSongIndex());
                } else {
                    currentSongPlayInfo = user.getLastLoadedPlaylist().getShuffledPlaylistSongs().
                            get(user.getLastLoadedPlaylist().getCurrentSongIndex());
                }
                passedTime = user.getLastLoadedPlaylist().getCurrentSongSecond();
                break;
            case TYPE_ALBUM:
                if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                    currentSongPlayInfo = user.getLastLoadedAlbum().getSongs().
                            get(user.getLastLoadedAlbum().getCurrentSongIndex());
                } else {
                    currentSongPlayInfo = user.getLastLoadedAlbum().getShuffledSongs().
                            get(user.getLastLoadedAlbum().getCurrentSongIndex());
                }
                passedTime = user.getLastLoadedAlbum().getCurrentSongSecond();
                break;
            default:
                break;
        }

        if (passedTime < MINIMUM_PASSED_SECONDS) {
            return "No new recommendations were found " + passedTime;
        }

        // create the list of songs that have the same genre as the current song
        ArrayList<SongPlayInfo> songsWithSameGenre = new ArrayList<>();
        for (SongPlayInfo song : Main.songsList) {
            if (song.getSong().getGenre().equals(currentSongPlayInfo.
                    getSong().getGenre())) {
                songsWithSameGenre.add(song);
            }
        }

        if (songsWithSameGenre.isEmpty()) {
            return "No new recommendations were found";
        }

        Random random = new Random(passedTime);
        int randomIndex = random.nextInt(songsWithSameGenre.size());
        SongPlayInfo randomSong = songsWithSameGenre.get(randomIndex);
        user.setLastSongRecommendation(randomSong);
        user.getSongRecommendations().add(randomSong.getSong().getName());
        user.setLastRecommendationType("random_song");
        return "The recommendations for user " + this.getUsername()
                + " have been updated successfully.";
    }

    /**
     * Gets a random playlist recommendation for the user.
     *
     * @param user the normal user.
     * @return a message indicating the success of the operation.
     */
    public String getRandomPlaylistRecommendation(final NormalUser user) {
        // use the logic from the wrapped command
        /* create a list of wrapped genres for the current user: the "listens"
        field will contain the sum of the number of likes + appearances in
        playlists created by the user + appearances in playlists followed
        by the user */
        ArrayList<WrappedGenre> userWrappedGenres = new ArrayList<>();

        // browse through the user's liked songs
        for (SongPlayInfo song : user.getLikedSongs()) {
            user.updateWrappedGenres(song.getSong().getGenre(), userWrappedGenres);
        }

        // browse through the playlists created by the user
        for (Playlist playlist : user.getPlaylists()) {
            for (SongPlayInfo song : playlist.getPlaylistSongs()) {
                user.updateWrappedGenres(song.getSong().getGenre(), userWrappedGenres);
            }
        }

        // browse through the playlists followed by the user
        for (Playlist playlist : user.getFollowing()) {
            for (SongPlayInfo song : playlist.getPlaylistSongs()) {
                user.updateWrappedGenres(song.getSong().getGenre(), userWrappedGenres);
            }
        }

        // sort the list of genres
        ArrayList<WrappedGenre> top3Genres = getTop3GenresList(userWrappedGenres);

        if (top3Genres.isEmpty()) {
            return "No new recommendations were found";
        }

        // create a new playlist
        Playlist newPlaylistRecommendation = createRandomPlaylist(top3Genres);

        user.setRandomPlaylistRecommendation(newPlaylistRecommendation);
        user.setLastRecommendationType("random_playlist");
        return "The recommendations for user " + this.getUsername()
                + " have been updated successfully.";
    }

    /**
     * Creates a random playlist based on the user's top genres.
     *
     * @param top3Genres the top 3 genres.
     * @return the created playlist.
     */
    public Playlist createRandomPlaylist(final ArrayList<WrappedGenre> top3Genres) {
        // create a new playlist
        String newPlaylistName = this.getUsername() + "'s recommendations";
        Playlist randomPlaylist = new Playlist(newPlaylistName, false, this.getUsername());

        // create a list with all the songs whose genre is the first one
        ArrayList<SongPlayInfo> songsWithFirstGenre = new ArrayList<>();

        // create a list with all the songs whose genre is the second one
        ArrayList<SongPlayInfo> songsWithSecondGenre = new ArrayList<>();

        // create a list with all the songs whose genre is the third one
        ArrayList<SongPlayInfo> songsWithThirdGenre = new ArrayList<>();

        for (SongPlayInfo librarySong : Main.songsList) {
            if (librarySong.getSong().getGenre().equals(top3Genres.get(0).getGenre())) {
                songsWithFirstGenre.add(librarySong);
            }
            if (top3Genres.size() >= 2) {
                if (librarySong.getSong().getGenre().equals(top3Genres.get(1).getGenre())) {
                    songsWithSecondGenre.add(librarySong);
                }

                if (top3Genres.size() >= NUMBER_OF_RANKED_GENRES) {
                    if (librarySong.getSong().getGenre().equals(top3Genres.get(2).getGenre())) {
                        songsWithThirdGenre.add(librarySong);
                    }
                }
            }
        }

        // sort all the 3 lists of songs by number of likes
        songsWithFirstGenre = sortSongListByNumberOfLikes(songsWithFirstGenre);
        // add the first 5 songs from the first list to the playlist
        if (songsWithFirstGenre.size() < NUMBER_OF_RANKED_SONGS) {
            randomPlaylist.getPlaylistSongs().addAll(songsWithFirstGenre);
        }
        for (int i = 0; i < NUMBER_OF_RANKED_SONGS; i++) {
            randomPlaylist.getPlaylistSongs().add(songsWithFirstGenre.get(i));
        }

        if (top3Genres.size() >= 2) {
            songsWithSecondGenre = sortSongListByNumberOfLikes(songsWithSecondGenre);
            // add the first 3 songs from the second list to the playlist
            if (songsWithSecondGenre.size() < NUMBER_OF_RANKED_GENRES) {
                randomPlaylist.getPlaylistSongs().addAll(songsWithSecondGenre);
            }
            for (int i = 0; i < NUMBER_OF_RANKED_GENRES; i++) {
                randomPlaylist.getPlaylistSongs().add(songsWithSecondGenre.get(i));
            }

            if (top3Genres.size() >= NUMBER_OF_RANKED_GENRES) {
                songsWithThirdGenre = sortSongListByNumberOfLikes(songsWithThirdGenre);
                // add the first 2 songs from the third list to the playlist
                if (songsWithThirdGenre.size() < 2) {
                    randomPlaylist.getPlaylistSongs().addAll(songsWithThirdGenre);
                }
                for (int i = 0; i < 2; i++) {
                    randomPlaylist.getPlaylistSongs().add(songsWithThirdGenre.get(i));
                }
            }
        }
        return randomPlaylist;
    }

    /**
     * Sorts a list of songs by the number of likes.
     *
     * @param songList the list of songs to sort.
     * @return the sorted list of songs.
     */
    ArrayList<SongPlayInfo> sortSongListByNumberOfLikes(final ArrayList<SongPlayInfo> songList) {
        // sort the Songs list by the NumberOf Likes in descending order
        songList.sort(new Comparator<SongPlayInfo>() {
            @Override
            public int compare(final SongPlayInfo song1, final SongPlayInfo song2) {
                return song2.getNumberOfLikes() - song1.getNumberOfLikes();
            }
        });

        return songList;
    }

    /**
     * Gets the top 3 genres from a list of user-wrapped genres.
     *
     * @param userWrappedGenres the list of user-wrapped genres.
     * @return the top 3 genres.
     */
    public ArrayList<WrappedGenre> getTop3GenresList(final ArrayList<WrappedGenre>
                                                             userWrappedGenres) {
        /* sort the user's wrapped artist list by count of plays in descending
        order and in case of equality, sort lexicographically */
        userWrappedGenres.sort(new Comparator<WrappedGenre>() {
            @Override
            public int compare(final WrappedGenre wrappedGenre1, final WrappedGenre wrappedGenre2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedGenre1.getListens(), wrappedGenre2.getListens())) {
                    return wrappedGenre1.getGenre().compareTo(wrappedGenre2.getGenre());
                }
                return wrappedGenre2.getListens() - wrappedGenre1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedGenre> topGenresList = new ArrayList<>();

        // check if we have less than 3 genres
        if (userWrappedGenres.size() < MAX_RESULTS) {
            for (int i = 0; i < userWrappedGenres.size(); i++) {
                topGenresList.add(userWrappedGenres.get(i));
            }
            return topGenresList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topGenresList.add(userWrappedGenres.get(i));
        }
        return topGenresList;
    }

    /**
     * Gets a fans playlist recommendation for the user.
     *
     * @param user the normal user.
     * @return a message indicating the success of the operation.
     */
    public String getFansPlaylistRecommendation(final NormalUser user) {
        SongPlayInfo currentSongPlayInfo = identifyLastLoadedSong(user);

        // identify the artist of the current song
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(currentSongPlayInfo.getSong().getArtist())) {
                String playlistName = artist.getUsername() + " Fan Club recommendations";
                Playlist fansPlaylistRecommendations = new Playlist(playlistName, false,
                        user.getUsername());
                ArrayList<WrappedFan> artistFans =  getTopFansList(getArtistFans(artist));
                if (artistFans.isEmpty()) {
                    return "No new recommendations were found";
                }

                // find top 5 songs according to the number of likes for each fan
                for (WrappedFan fan : artistFans) {
                    ArrayList<SongPlayInfo> fansSongs = getTruncatedResultTop5Songs(
                            getSortedSongsByLikes(fan.getNormalUser().getLikedSongs()));
                    for (SongPlayInfo fanSong : fansSongs) {
                        if (!fansPlaylistRecommendations.getPlaylistSongs().contains(fanSong)) {
                            fansPlaylistRecommendations.getPlaylistSongs().add(fanSong);
                        }
                    }
                }

                user.setFansPlaylistRecommendations(fansPlaylistRecommendations);
                user.setLastRecommendationType("fans_playlist");
                return "The recommendations for user " + this.getUsername()
                        + " have been updated successfully.";
            }
        }
        return null;
    }

    /**
     * Gets the message associated with the command.
     *
     * @return the message.
     */
    public String getCommandMessage() {
        // look for the user in the normal users list
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                // check if the user has loaded a song / playlist / album
                if (!user.getLoaded()) {
                    return "No new recommendations were found";
                }

                if (user.getLastLoadTypeIndicator() == TYPE_PODCAST) {
                    return "No new recommendations were found";
                }

                // call the specific method according to the recommendation type
                updateLastLoadedEntity(user);
                switch (this.getRecommendationType()) {
                    case "random_song":
                        return getRandomSongRecommendation(user);
                    case  "random_playlist":
                        return getRandomPlaylistRecommendation(user);
                    case "fans_playlist":
                        return getFansPlaylistRecommendation(user);
                    default:
                        break;
                }
            }
        }

        /* if the user has not been found in the normal users list, look
        for it in the list of artists and in the list of hosts to see if
        the given username exists, but it doesn't belong to a normal
        user or it doesn't exist at all */
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                return this.getUsername() + " is not a normal user.";
            }
        }

        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.getUsername())) {
                return this.getUsername() + " is not a normal user.";
            }
        }

        // if we reached this point, it means that the username doesn't exist at all
        return "The username " + this.getUsername() + " doesn't exist.";
    }

    /**
     * Executes the command and returns the result as a JSON node.
     *
     * @return the JSON node containing information about the command execution.
     */
    @Override
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", getCommandMessage());
        return node;
    }

    /**
     * Gets the type of recommendation.
     *
     * @return the recommendation type.
     */
    public String getRecommendationType() {
        return recommendationType;
    }

    /**
     * Sets the type of recommendation.
     *
     * @param recommendationType the recommendation type to set.
     */
    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }

    /**
     * Gets the username associated with the command.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the command.
     *
     * @param username the username to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}
