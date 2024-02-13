package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.PodcastInput;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;
import fileio.input.wrappedEntities.WrappedEpisode;
import fileio.input.wrappedEntities.WrappedGenre;
import fileio.input.wrappedEntities.WrappedAlbum;
import fileio.input.wrappedEntities.WrappedSong;
import fileio.input.wrappedEntities.WrappedArtist;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class WrappedCommand extends StandardStatisticsCommand {
    private String username;
    private static WrappedCommand instance = null;
    private static final int MAX_RESULTS = 5;

    // make constructor private for singleton implementation
    private WrappedCommand(final String command, final String username,
                                    final Integer timestamp,
                                     final ObjectNode node) {
        super(command, timestamp, node);
        this.username = username;
    }

    /**
     * Gets the instance of WrappedCommand (Singleton pattern).
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node containing the command information.
     * @return The instance of WrappedCommand.
     */
    public static WrappedCommand getInstance(final String command, final String username,
                                                      final Integer timestamp,
                                                      final ObjectNode node) {
        if (instance == null) {
            instance = new WrappedCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Gets the top artists list for a given user.
     *
     * @param user The NormalUser for whom the top artists list is retrieved.
     * @return The list of top artists.
     */
    public ArrayList<WrappedArtist> getTopArtistsList(final NormalUser user) {
        /* sort the user's wrapped artist list by count of plays in descending
        order and in case of equality, sort lexicographically */
        user.getWrappedArtists().sort(new Comparator<WrappedArtist>() {
            @Override
            public int compare(final WrappedArtist wrappedArtist1,
                               final WrappedArtist wrappedArtist2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedArtist1.getListens(), wrappedArtist2.getListens())) {
                    return wrappedArtist1.getArtistName().compareTo(wrappedArtist2.getArtistName());
                }
                return wrappedArtist2.getListens() - wrappedArtist1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedArtist> topArtistsList = new ArrayList<>();

        // check if we have less than 5 artists
        if (user.getWrappedArtists().size() < MAX_RESULTS) {
            for (int i = 0; i < user.getWrappedArtists().size(); i++) {
                topArtistsList.add(user.getWrappedArtists().get(i));
            }
            return topArtistsList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topArtistsList.add(user.getWrappedArtists().get(i));
        }
        return topArtistsList;
    }

    /**
     * Gets the top songs list for a given list of wrapped songs.
     *
     * @param wrappedSongList The list of wrapped songs to be considered.
     * @return The list of top songs.
     */
    public ArrayList<WrappedSong> getTopSongsList(final ArrayList<WrappedSong> wrappedSongList) {
        /* sort the user's wrapped songs list by count of plays in
        descending order and in case of equality, sort lexicographically */
        wrappedSongList.sort(new Comparator<WrappedSong>() {
            @Override
            public int compare(final WrappedSong wrappedSong1, final WrappedSong wrappedSong2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedSong1.getListens(), wrappedSong2.getListens())) {
                    return wrappedSong1.getSongPlayInfo().getSong().getName().compareTo(
                            wrappedSong2.getSongPlayInfo().getSong().getName());
                }
                return wrappedSong2.getListens() - wrappedSong1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedSong> topSongsList = new ArrayList<>();

        // check if we have less than 5 artists
        if (wrappedSongList.size() < MAX_RESULTS) {
            for (int i = 0; i < wrappedSongList.size(); i++) {
                topSongsList.add(wrappedSongList.get(i));
            }
            return topSongsList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topSongsList.add(wrappedSongList.get(i));
        }
        return topSongsList;
    }

    /**
     * Gets the top genres list for a given user.
     *
     * @param user The NormalUser for whom the top genres list is retrieved.
     * @return The list of top genres.
     */
    public ArrayList<WrappedGenre> getTopGenresList(final NormalUser user) {
        /* sort the user's wrapped genres list by count of plays in
        descending order and in case of equality, sort lexicographically */
        user.getWrappedGenres().sort(new Comparator<WrappedGenre>() {
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

        // check if we have less than 5 genres
        if (user.getWrappedGenres().size() < MAX_RESULTS) {
            for (int i = 0; i < user.getWrappedGenres().size(); i++) {
                topGenresList.add(user.getWrappedGenres().get(i));
            }
            return topGenresList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topGenresList.add(user.getWrappedGenres().get(i));
        }
        return topGenresList;
    }

    /**
     * Gets the top episodes list for a given list of wrapped episodes.
     *
     * @param wrappedEpisodesList The list of wrapped episodes to be considered.
     * @return The list of top episodes.
     */
    public ArrayList<WrappedEpisode> getTopEpisodesList(final ArrayList<WrappedEpisode>
                                                                wrappedEpisodesList) {
        /* sort the user's wrapped episodes list by count of plays in descending order
        and in case of equality, sort lexicographically */
        wrappedEpisodesList.sort(new Comparator<WrappedEpisode>() {
            @Override
            public int compare(final WrappedEpisode wrappedEpisode1,
                               final WrappedEpisode wrappedEpisode2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedEpisode1.getListens(), wrappedEpisode2.getListens())) {
                    return wrappedEpisode1.getEpisode().getName().compareTo(
                            wrappedEpisode2.getEpisode().getName());
                }
                return wrappedEpisode2.getListens() - wrappedEpisode1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedEpisode> topEpisodesList = new ArrayList<>();

        // check if we have less than 5 artists
        if (wrappedEpisodesList.size() < MAX_RESULTS) {
            for (int i = 0; i < wrappedEpisodesList.size(); i++) {
                topEpisodesList.add(wrappedEpisodesList.get(i));
            }
            return topEpisodesList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topEpisodesList.add(wrappedEpisodesList.get(i));
        }
        return topEpisodesList;
    }

    /**
     * Gets the top albums list for a given list of wrapped albums.
     *
     * @param wrappedAlbumsList The list of wrapped albums to be considered.
     * @return The list of top albums.
     */
    public ArrayList<WrappedAlbum> getTopAlbumsList(final ArrayList<WrappedAlbum>
                                                            wrappedAlbumsList) {
        /* sort the user's wrapped albums list by count of plays
        in descending order and in case of equality, sort lexicographically */
        wrappedAlbumsList.sort(new Comparator<WrappedAlbum>() {
            @Override
            public int compare(final WrappedAlbum wrappedAlbum1, final WrappedAlbum wrappedAlbum2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedAlbum1.getListens(), wrappedAlbum2.getListens())) {
                    return wrappedAlbum1.getAlbumName().compareTo(wrappedAlbum2.getAlbumName());
                }
                return wrappedAlbum2.getListens() - wrappedAlbum1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedAlbum> topAlbumsList = new ArrayList<>();

        // check if we have less than 5 artists
        if (wrappedAlbumsList.size() < MAX_RESULTS) {
            for (int i = 0; i < wrappedAlbumsList.size(); i++) {
                topAlbumsList.add(wrappedAlbumsList.get(i));
            }
            return topAlbumsList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topAlbumsList.add(wrappedAlbumsList.get(i));
        }
        return topAlbumsList;
    }

    /**
     * Gets a JSON node representing the top artists and their listens for a given user.
     *
     * @param user The NormalUser for whom the top artists node is retrieved.
     * @return The JSON node representing the top artists and listens.
     */
    public ObjectNode getTopArtistsNode(final NormalUser user) {
        ObjectNode artistsResult = JsonNodeFactory.instance.objectNode();
        for (WrappedArtist wrappedArtist: getTopArtistsList(user)) {
            artistsResult.put(wrappedArtist.getArtistName(),
                    wrappedArtist.getListens());
        }
        return artistsResult;
    }

    /**
     * Gets a JSON node representing the top songs and their listens for
     * a given list of wrapped songs.
     *
     * @param wrappedSongList The list of wrapped songs for which the top songs node is retrieved.
     * @return The JSON node representing the top songs and listens.
     */
    public ObjectNode getTopSongsNode(final ArrayList<WrappedSong> wrappedSongList) {
        ObjectNode songsResult = JsonNodeFactory.instance.objectNode();
        for (WrappedSong wrappedSong: getTopSongsList(wrappedSongList)) {
            songsResult.put(wrappedSong.getSongPlayInfo().getSong().getName(),
                    wrappedSong.getListens());
        }
        return songsResult;
    }

    /**
     * Gets a JSON node representing the top genres and their listens for a given user.
     *
     * @param user The NormalUser for whom the top genres node is retrieved.
     * @return The JSON node representing the top genres and listens.
     */
    public ObjectNode getTopGenresNode(final NormalUser user) {
        ObjectNode genresResult = JsonNodeFactory.instance.objectNode();
        for (WrappedGenre wrappedGenre: getTopGenresList(user)) {
            genresResult.put(wrappedGenre.getGenre(), wrappedGenre.getListens());
        }
        return genresResult;
    }

    /**
     * Gets a JSON node representing the top episodes and their listens for
     * a given list of wrapped episodes.
     *
     * @param wrappedEpisodesList The list of wrapped episodes for which the
     *                            top episodes node is retrieved.
     * @return The JSON node representing the top episodes and listens.
     */
    public ObjectNode getTopEpisodesNode(final ArrayList<WrappedEpisode>
                                                 wrappedEpisodesList) {
        ObjectNode episodesResult = JsonNodeFactory.instance.objectNode();
        for (WrappedEpisode wrappedEpisode: getTopEpisodesList(wrappedEpisodesList)) {
            episodesResult.put(wrappedEpisode.getEpisode().getName(),
                    wrappedEpisode.getListens());
        }
        return episodesResult;
    }

    /**
     * Gets a JSON node representing the top albums and their listens for a
     * given list of wrapped albums.
     *
     * @param wrappedAlbumsList The list of wrapped albums for which the
     *                          top albums node is retrieved.
     * @return The JSON node representing the top albums and listens.
     */
    public ObjectNode getTopAlbumsNode(final ArrayList<WrappedAlbum> wrappedAlbumsList) {
        ObjectNode albumsResult = JsonNodeFactory.instance.objectNode();
        for (WrappedAlbum wrappedAlbum: wrappedAlbumsList) {
            albumsResult.put(wrappedAlbum.getAlbumName(), wrappedAlbum.getListens());
        }
        return albumsResult;
    }

    /**
     * Gets a list of wrapped albums for a given artist.
     *
     * @param artist The artist for whom the wrapped albums list is retrieved.
     * @return The list of wrapped albums.
     */
    public ArrayList<WrappedAlbum> getArtistWrappedAlbums(final Artist artist) {
        // get the list of albums paired with their number of listens
        ArrayList<WrappedAlbum> artistWrappedAlbums = new ArrayList<>();

        for (Album album: artist.getAlbums()) {
            // populate the list with all the albums of the artist
            WrappedAlbum artistWrappedAlbum = new WrappedAlbum(album.getName());
            artistWrappedAlbum.setListens(0);
            artistWrappedAlbums.add(artistWrappedAlbum);

            updateAlbumListens(album, artistWrappedAlbum);
        }
        return artistWrappedAlbums;
    }

    /**
     * Updates the listens for a given album based on the listens from all users.
     *
     * @param album              The album for which the listens are updated.
     * @param artistWrappedAlbum The wrapped album object containing the listens.
     */
    public void updateAlbumListens(final Album album, final WrappedAlbum
            artistWrappedAlbum) {
        // look for the album in the wrappedLists of all users
        for (NormalUser user : Main.normalUserList) {
            for (WrappedAlbum userWrappedAlbum : user.getWrappedAlbums()) {
                if (userWrappedAlbum.getAlbumName().equals(album.getName())) {
                    artistWrappedAlbum.setListens(userWrappedAlbum.getListens()
                            + artistWrappedAlbum.getListens());
                }
            }
        }
    }

    /**
     * Gets a list of wrapped songs for a given artist.
     *
     * @param artist The artist for whom the wrapped songs list is retrieved.
     * @return The list of wrapped songs.
     */
    public ArrayList<WrappedSong> getArtistWrappedSongs(final Artist artist) {
        ArrayList<WrappedSong> artistWrappedSongs = new ArrayList<>();

        for (NormalUser user : Main.normalUserList) {
            for (WrappedSong userWrappedSong : user.getWrappedSongs()) {
                if (userWrappedSong.getSongPlayInfo().getSong().getArtist().
                        equals(artist.getUsername())) {
                    updateListensForSong(artistWrappedSongs, userWrappedSong);
                }
            }
        }

        return artistWrappedSongs;
    }

    /**
     * Updates the listens for a given song based on the listens from all users.
     *
     * @param artistWrappedSongs The list of wrapped songs for the artist.
     * @param userWrappedSong    The wrapped song object containing the listens.
     */
    public void updateListensForSong(final ArrayList<WrappedSong> artistWrappedSongs,
                                     final WrappedSong userWrappedSong) {
        boolean found = false;
        for (WrappedSong artistWrappedSong : artistWrappedSongs) {
            if (artistWrappedSong.getSongPlayInfo().getSong().getName().
                    equals(userWrappedSong.
                    getSongPlayInfo().getSong().getName())) {
                artistWrappedSong.setListens(artistWrappedSong.getListens()
                        + userWrappedSong.getListens());
                found = true;
                break;
            }
        }

        if (!found) {
            WrappedSong artistWrappedSong = new WrappedSong(userWrappedSong.getSongPlayInfo());
            artistWrappedSong.setListens(userWrappedSong.getListens());
            artistWrappedSongs.add(artistWrappedSong);
        }
    }


    /**
     * Gets a list of wrapped episodes for a given host.
     *
     * @param host The host for whom the wrapped episodes list is retrieved.
     * @return The list of wrapped episodes.
     */
    public ArrayList<WrappedEpisode> getHostWrappedEpisodes(final Host host) {
        ArrayList<WrappedEpisode> hostWrappedEpisodes = new ArrayList<>();

        for (PodcastInput hostPodcast : host.getPodcasts()) {
            for (EpisodeInput episode : hostPodcast.getEpisodes()) {
                // add all episodes of the user to the list
                WrappedEpisode wrappedEpisode = new WrappedEpisode(episode);
                wrappedEpisode.setListens(0);
                hostWrappedEpisodes.add(wrappedEpisode);

                updateAllEpisodesListens(wrappedEpisode, episode);
            }
        }
        return hostWrappedEpisodes;
    }

    /**
     * Updates the listens for a given episode based on the listens from all users.
     *
     * @param wrappedEpisode The wrapped episode object containing the listens.
     * @param episode        The original episode input.
     */
    public void updateAllEpisodesListens(final WrappedEpisode wrappedEpisode,
                                         final EpisodeInput episode) {
        // update each episode's number of listens
        for (NormalUser user : Main.normalUserList) {
            updateStats(user);
            for (WrappedEpisode userWrappedEpisode : user.getWrappedEpisodes()) {
                if (userWrappedEpisode.getEpisode().getName().
                        equals(episode.getName())) {
                    wrappedEpisode.setListens(wrappedEpisode.getListens()
                            + userWrappedEpisode.getListens());
                }
            }
        }
    }

    /**
     * Removes wrapped episodes with zero listens from the list.
     *
     * @param wrappedEpisodesList The list of wrapped episodes to be considered.
     * @return The list of wrapped episodes with non-zero listens.
     */
    public ArrayList<WrappedEpisode> removeRedundantWrappedEpisodes(
            final ArrayList<WrappedEpisode> wrappedEpisodesList) {
        ArrayList<WrappedEpisode> keepWrappedEpisodes = new ArrayList<>();
        for (WrappedEpisode wrappedEpisode : wrappedEpisodesList) {
            if (wrappedEpisode.getListens() != 0) {
                keepWrappedEpisodes.add(wrappedEpisode);
            }
        }
        return keepWrappedEpisodes;
    }


    /**
     * Inner class implementing the VisitorWrapped interface for executing
     * commands based on user type.
     */
    public class WrappedExecutionAccordingToUSerType implements VisitorWrapped {

        /**
         * Generates a JSON node representing the top artists, genres, songs,
         * and albums for a given NormalUser.
         *
         * @param user The NormalUser for whom the JSON node is generated.
         */
        @Override
        public void getWrappedResultNode(final NormalUser user) {
            updateStats(user);

            // check if the user has loaded anything from timestamp 0 until present
            if (user.getWrappedArtists().isEmpty() && user.getWrappedAlbums().isEmpty()
                    && user.getWrappedSongs().isEmpty() && user.getWrappedEpisodes().
                    isEmpty()) {
                node.put("message", "No data to show for user "
                        + getUsername() + ".");
                return;
            }

            ObjectNode result = JsonNodeFactory.instance.objectNode();

            result.put("topArtists", getTopArtistsNode(user));
            result.put("topGenres", getTopGenresNode(user));
            result.put("topSongs", getTopSongsNode(user.getWrappedSongs()));
            result.put("topAlbums", getTopAlbumsNode(getTopAlbumsList(
                    user.getWrappedAlbums())));

            result.put("topEpisodes", getTopEpisodesNode(user.getWrappedEpisodes()));
            node.putPOJO("result", result);
        }


        /**
         * Generates a JSON node representing the top albums, songs,
         * and fans for a given Artist.
         *
         * @param artist The Artist for whom the JSON node is generated.
         */
        @Override
        public void getWrappedResultNode(final Artist artist) {
            ObjectNode result = JsonNodeFactory.instance.objectNode();

            for (NormalUser user : Main.normalUserList) {
                updateStats(user);
            }

            ArrayList<WrappedSong> artistWrappedSongs = getArtistWrappedSongs(artist);
            ArrayList<WrappedAlbum> artistWrappedAlbums = getArtistWrappedAlbums(artist);

            if (artistWrappedAlbums.isEmpty() && getArtistWrappedSongs(artist).isEmpty()
                    && getArtistFans(artist).isEmpty()) {
                node.put("message", "No data to show for artist " + getUsername() + ".");
                return;
            }

            result.put("topAlbums", getTopAlbumsNode(
                    getTopAlbumsList(artist.getWrappedAlbums())));
            result.put("topSongs", getTopSongsNode(
                    getTopSongsList(artistWrappedSongs)));
            result.putPOJO("topFans", getTopFansListString(getTopFansList(
                    getArtistFans(artist))));
            result.put("listeners", getArtistFans(artist).size());
            node.putPOJO("result", result);
        }

        /**
         * Generates a JSON node representing the top episodes and listeners for a given Host.
         *
         * @param host The Host for whom the JSON node is generated.
         */
        @Override
        public void getWrappedResultNode(final Host host) {
            ObjectNode result = JsonNodeFactory.instance.objectNode();

            result.put("topEpisodes", getTopEpisodesNode(getTopEpisodesList(
                    removeRedundantWrappedEpisodes(getHostWrappedEpisodes(host)))));
            result.put("listeners", host.getListeners().size());

            node.putPOJO("result", result);

        }
    }

    /**
     * Executes the appropriate command based on the user type (NormalUser, Artist, Host).
     */
    public void executeCommandAccordingToUserType() {
        // check if the user is a normal one
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                WrappedExecutionAccordingToUSerType wrappedExecutionAccordingToUSerType;
                wrappedExecutionAccordingToUSerType = new
                        WrappedExecutionAccordingToUSerType();
                wrappedExecutionAccordingToUSerType.getWrappedResultNode(user);
                return;
            }
        }

        // check if the user is an artist
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                WrappedExecutionAccordingToUSerType wrappedExecutionAccordingToUSerType;
                wrappedExecutionAccordingToUSerType = new
                        WrappedExecutionAccordingToUSerType();
                wrappedExecutionAccordingToUSerType.getWrappedResultNode(artist);
                return;
            }
        }

        // check if the user is a host
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.getUsername())) {
                WrappedExecutionAccordingToUSerType wrappedExecutionAccordingToUSerType;
                wrappedExecutionAccordingToUSerType = new
                        WrappedExecutionAccordingToUSerType();
                wrappedExecutionAccordingToUSerType.getWrappedResultNode(host);
                return;
            }
        }

        // if we reached this point, it means that the user does not exist
        node.put("message", "No data to show for user " + this.getUsername());
    }

    /**
     * Executes the appropriate command based on the user type (NormalUser, Artist, Host).
     * Generates a JSON node with the result of the executed command.
     *
     * @return The JSON node containing the result of the executed command.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        executeCommandAccordingToUserType();


        return node;
    }

    /**
     * Retrieves the username associated with this WrappedCommand instance.
     *
     * @return The username associated with this instance.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with this WrappedCommand instance.
     *
     * @param username The username to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}
