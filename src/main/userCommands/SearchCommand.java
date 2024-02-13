package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.Filters;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.PodcastInput;
import fileio.input.audioEntities.PodcastPlayInfo;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;

import java.util.ArrayList;

public final class SearchCommand extends StandardCommandForUserPlayer {
    private static final int MAX_SEARCH_RESULTS = 5;
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;
    private static final int TYPE_ARTIST = 5;
    private static final int TYPE_HOST = 6;

    private final LibraryInput library;
    private String type;
    private Filters filters;

    /**
     * Retrieves the library associated with this SearchCommand.
     *
     * @return the LibraryInput object associated with this SearchCommand.
     */
    public LibraryInput getLibrary() {
        return library;
    }

    /**
     * Retrieves the type of media entities to be searched (e.g., "song", "podcast", "playlist").
     *
     * @return a String representing the type of media entities to search.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the filters to be applied during the search.
     *
     * @return the Filters object containing the search criteria.
     */
    public Filters getFilters() {
        return filters;
    }

    /**
     * Sets the type of media entities to be searched.
     *
     * @param type a String representing the type of media entities to search.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Sets the filters to be applied during the search.
     *
     * @param filters the Filters object containing the search criteria.
     */
    public void setFilters(final Filters filters) {
        this.filters = filters;
    }

    // instance field that will help implement the singleton design pattern
    private static SearchCommand instance = null;

    /**
     * Gets the singleton instance of SearchCommand.
     *
     * @return the singleton instance of SearchCommand.
     */
    public static SearchCommand getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of SearchCommand.
     *
     * @param instance the singleton instance of SearchCommand.
     */
    public static void setInstance(final SearchCommand instance) {
        SearchCommand.instance = instance;
    }

    /**
     * Constructs a new SearchCommand with the specified parameters.
     *
     * @param library   the LibraryInput associated with this SearchCommand.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param type      the type of media entities to be searched.
     * @param filters   the Filters object containing search criteria.
     * @param node      the JSON node associated with the command.
     */

    // make constructor private for the singleton design pattern
    private SearchCommand(final LibraryInput library, final String command, final String username,
                          final Integer timestamp, final String type, final Filters filters,
                          final ObjectNode node) {
        super(command, timestamp, node, username);
        this.library = library;
        this.type = type;
        this.filters = filters;
    }

    /**
     * Gets the singleton instance of SearchCommand.
     *
     * @param library   the LibraryInput associated with the command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param type      the type of media entities to be searched.
     * @param filters   the Filters object containing search criteria.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of SearchCommand.
     */
    public static SearchCommand getInstance(final LibraryInput library, final String command,
                                            final String username,
                                            final Integer timestamp, final String type,
                                            final Filters filters, final ObjectNode node) {
        if (instance == null) {
            instance = new SearchCommand(library, command, username, timestamp,
                    type, filters, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setNode(node);
            instance.setTimestamp(timestamp);
            instance.setType(type);
            instance.setFilters(filters);
        }
        return instance;
    }



    /**
     * Generates a JSON node containing search results information.
     *
     * @return the JSON node representing search results.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            ArrayList<String> result = new ArrayList<>();
            node.putPOJO("results", result);
            return node;
        }

        /* the search command removes the last loaded entity from the player, so, before
        conducting another search, we need to update the status of the last audio entity
        loaded in the player for the wrapped command */
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                updateLastLoadedEntity(user);
            }
        }

        switch (this.getType()) {
            case "song":
                SearchCommand.SearchCommandForSongs searchCommandForSongs;
                searchCommandForSongs = this.new SearchCommandForSongs();
                ArrayList<SongPlayInfo> resultSong =
                        searchCommandForSongs.executeSearch(Main.songsList);
                node.put("message", "Search returned " + resultSong.size() + " results");
                node.putPOJO("results", searchCommandForSongs.getResultsNames(resultSong));
                break;
            case "podcast":
                SearchCommand.SearchCommandForPodcasts searchCommandForPodcasts;
                searchCommandForPodcasts = this.new SearchCommandForPodcasts();
                ArrayList<PodcastInput> resultPodcast =
                        searchCommandForPodcasts.executeSearch(Main.podcastsList);
                node.put("message", "Search returned " + resultPodcast.size() + " results");
                node.putPOJO("results", searchCommandForPodcasts.getResultsNames(resultPodcast));
                break;
            case "playlist":
                for (NormalUser user : Main.normalUserList) {
                    if (user.getUsername().equals(this.getUsername())) {
                        // search in the playlists owned by the user
                        SearchCommand.SearchCommandForPlaylists searchCommandForPlaylists;
                        searchCommandForPlaylists = this.new SearchCommandForPlaylists();
                        ArrayList<Playlist> resultPlaylist =
                                searchCommandForPlaylists.executeSearch(user.getPlaylists());
                        node.put("message", "Search returned " + resultPlaylist.size()
                                + " results");
                        node.putPOJO("results",
                                searchCommandForPlaylists.getResultsNames(resultPlaylist));
                    }
                }
                break;
            case "album":
                SearchCommand.SearchCommandForAlbums searchCommandForAlbums;
                searchCommandForAlbums = this.new SearchCommandForAlbums();
                ArrayList<Album> resultAlbum = searchCommandForAlbums.executeSearch();
                node.put("message", "Search returned " + resultAlbum.size() + " results");
                node.putPOJO("results", searchCommandForAlbums.getResultsNames(resultAlbum));
                break;
            case "artist":
                SearchCommand.SearchCommandForArtists searchCommandForArtists;
                searchCommandForArtists = this.new SearchCommandForArtists();
                ArrayList<Artist> resultArtist = searchCommandForArtists.executeSearch();
                node.put("message", "Search returned " + resultArtist.size() + " results");
                node.putPOJO("results", searchCommandForArtists.getResultsNames(resultArtist));
                break;
            case "host":
                SearchCommand.SearchCommandForHosts searchCommandForHosts;
                searchCommandForHosts = this.new SearchCommandForHosts();
                ArrayList<Host> resultHost = searchCommandForHosts.executeSearch();
                node.put("message", "Search returned " + resultHost.size() + " results");
                node.putPOJO("results", searchCommandForHosts.getResultsNames(resultHost));
                break;
            default:
                break;
        }

        // search acts like the pause command on podcasts running in the player

        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                user.setLoaded(false);
                user.setSelected(false);
                user.setLastSelectedSong(null);
                user.setLastSelectedPodcast(null);
                user.setLastSelectedPlaylist(null);
                user.setLastSelectedAlbum(null);
                // check if there is any podcast playing
                if (user.getLastLoadedPodcast() != null) {
                    if (!user.getLastLoadedPodcast().getPodcastPaused()) {
                        PodcastPlayInfo podcast = user.getLastLoadedPodcast();
                        podcast.updatePodcastStatus(user, this.getTimestamp());
                    }
                }

            }
        }

        return node;
    }

    public class SearchCommandForSongs {
        /**
         * Searches for songs in the given list based on specified filters.
         *
         * @param songList the list of songs to search within.
         * @return an ArrayList of SongInput objects that match the search criteria
         * (truncated to only max 5 elements).
         */
        public ArrayList<SongPlayInfo> executeSearch(final ArrayList<SongPlayInfo> songList) {
            ArrayList<SongPlayInfo> result = new ArrayList<>();
            for (SongPlayInfo songPlayInfo : songList) {
                if (filters.getName() != null) {
                    // name is one of the filters
                    if (!songPlayInfo.getSong().getName().toLowerCase().
                            startsWith(filters.getName().toLowerCase())) {
                        continue;
                    }
                }
                if (filters.getAlbum() != null) {
                    // album is one of the filters
                    if (!songPlayInfo.getSong().getAlbum().equalsIgnoreCase(filters.getAlbum())) {
                        continue;
                    }
                }
                if (filters.getTags() != null) {
                    // the tag list is one of the filters
                    if (!songPlayInfo.getSong().getTags().containsAll(filters.getTags())) {
                        continue;
                    }
                }
                if (filters.getLyrics() != null) {
                    // lyrics is one of the filters
                    if (!songPlayInfo.getSong().getLyrics().toLowerCase()
                            .contains(filters.getLyrics().toLowerCase())) {
                        continue;
                    }
                }
                if (filters.getGenre() != null) {
                    // genre is one of the filters
                    if (!songPlayInfo.getSong().getGenre().equalsIgnoreCase(filters.getGenre())) {
                        continue;
                    }
                }
                if (filters.getReleaseYear() != null) {
                    // extract the operator from the string
                    char operator = filters.getReleaseYear().charAt(0);
                    String digitStringReleaseYear = filters.getReleaseYear().substring(1);
                    Integer intValueOfReleaseYear = Integer.parseInt(digitStringReleaseYear);
                    if (operator == '>') {
                        if (songPlayInfo.getSong().getReleaseYear() <= intValueOfReleaseYear) {
                            continue;
                        }
                    } else {
                        if (operator == '<') {
                            if (songPlayInfo.getSong().getReleaseYear() >= intValueOfReleaseYear) {
                                continue;
                            }
                        } else {
                            // it means that the operator is '='
                            if (songPlayInfo.getSong().getReleaseYear() != intValueOfReleaseYear) {
                                continue;
                            }
                        }
                    }
                }
                if (filters.getArtist() != null) {
                    if (!songPlayInfo.getSong().getArtist().equalsIgnoreCase(filters.getArtist())) {
                        continue;
                    }
                }

                // if we have reached this point, it means that all the filters were respected
                result.add(songPlayInfo);
            }

            // keep only the first 5 songs that resulted from the search
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }

            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastSongSearchResult(result);
                    user.setSearched(true);
                    user.setLastSearchTypeIndicator(TYPE_SONG);
                }
            }
            return result;
        }

        /**
         * Retrieves the names of songs from the given list of SongInput objects.
         *
         * @param result the list of SongInput objects.
         * @return an ArrayList of song names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<SongPlayInfo> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (SongPlayInfo songPlayInfo : result) {
                resultsNames.add(songPlayInfo.getSong().getName());
            }
            return resultsNames;
        }
    }

    public class SearchCommandForPodcasts {
        /**
         * Searches for podcasts in the given list based on specified filters.
         *
         * @param podcastList the list of podcasts to search within.
         * @return an ArrayList of PodcastInput objects that match the search
         * criteria (truncated to only max 5 elements).
         */
        public ArrayList<PodcastInput> executeSearch(final ArrayList<PodcastInput> podcastList) {
            ArrayList<PodcastInput> result = new ArrayList<>();
            for (PodcastInput podcast : podcastList) {
                if (filters.getName() != null) {
                    // name is one of the filters
                    if (!podcast.getName().toLowerCase().startsWith(filters.
                            getName().toLowerCase())) {
                        continue;
                    }
                }
                if (filters.getOwner() != null) {
                    // owner is one of the filters
                    if (!podcast.getOwner().equalsIgnoreCase(filters.getOwner())) {
                        continue;
                    }
                }
                result.add(podcast);
            }

            // only keep the first 5 results of the search operation
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }

            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastPodcastSearchResult(result);
                    user.setLastSearchTypeIndicator(TYPE_PODCAST);
                    user.setSearched(true);
                }
            }

            return result;
        }

        /**
         * Retrieves the names of podcasts from the given list of PodcastInput objects.
         *
         * @param result the list of PodcastInput objects.
         * @return an ArrayList of podcast names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<PodcastInput> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (PodcastInput podcast : result) {
                resultsNames.add(podcast.getName());
            }
            return resultsNames;
        }
    }

    public class SearchCommandForPlaylists {
        /**
         * Searches for playlists in the given list based on specified filters.
         *
         * @param playlists the list of playlists to search within.
         * @return an ArrayList of Playlist objects that match the search criteria
         * (truncated to only max 5 elements).
         */
        public ArrayList<Playlist> executeSearch(final ArrayList<Playlist> playlists) {
            ArrayList<Playlist> result = new ArrayList<>();
            for (NormalUser user : Main.normalUserList) {
                for (Playlist playlist : user.getPlaylists()) {
                    if (!playlist.getVisibility() || playlist.getOwner().equals(username)) {
                        if (filters.getName() != null) {
                            // name is one of the filters
                            if (!playlist.getName().toLowerCase().
                                    startsWith(filters.getName().toLowerCase())) {
                                continue;
                            }
                        }
                        if (filters.getOwner() != null) {
                            // owner is one of the filters
                            if (!playlist.getOwner().equalsIgnoreCase(filters.getOwner())) {
                                continue;
                            }
                        }
                        result.add(playlist);
                    }
                }
            }

            // only keep the first 5 results of the search operation
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }
            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastPlaylistSearchResult(result);
                    user.setLastSearchTypeIndicator(TYPE_PLAYLIST);
                    user.setSearched(true);
                }
            }
            return result;
        }

        /**
         * Retrieves the names of playlists from the given list of Playlist objects.
         *
         * @param result the list of Playlist objects.
         * @return an ArrayList of playlist names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<Playlist> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (Playlist playlist : result) {
                resultsNames.add(playlist.getName());
            }
            return resultsNames;
        }
    }

    public class SearchCommandForAlbums {
        /**
         * Searches for albums in the given list based on specified filters.
         *
         * @return an ArrayList of Album objects that match the search criteria
         * (truncated to only max 5 elements).
         */
        public ArrayList<Album> executeSearch() {
            ArrayList<Album> result = new ArrayList<>();
            for (Artist artist : Main.artistsList) {
                if (filters.getOwner() != null) {
                    if (!artist.getUsername().toLowerCase().
                            startsWith(filters.getOwner().toLowerCase())) {
                        continue;
                    }
                }
                for (Album album : artist.getAlbums()) {
                    if (filters.getName() != null) {
                        // name is one of the filters
                        if (!album.getName().toLowerCase().
                                startsWith(filters.getName().toLowerCase())) {
                            continue;
                        }
                    }
                    if (filters.getDescription() != null) {
                        // description is one of the filters
                        if (!album.getDescription().toLowerCase().
                                startsWith(filters.getDescription().toLowerCase())) {
                            continue;
                        }
                    }

                    /* if we reached this point, it means that all the
                    filters were respected */
                    result.add(album);
                }
            }

            // only keep the first 5 results of the search operation
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }

            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastAlbumSearchResult(result);
                    user.setLastSearchTypeIndicator(TYPE_ALBUM);
                    user.setSearched(true);
                }
            }

            return result;
        }

        /**
         * Retrieves the names of albums from the given list of Album objects.
         *
         * @param result the list of Album objects.
         * @return an ArrayList of album names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<Album> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (Album album : result) {
                resultsNames.add(album.getName());
            }
            return resultsNames;
        }
    }

    public class SearchCommandForArtists {
        /**
         * Searches for artists in the given list based on specified filters.
         *
         * @return an ArrayList of Artist objects that match the search criteria
         * (truncated to only max 5 elements).
         */
        public ArrayList<Artist> executeSearch() {
            ArrayList<Artist> result = new ArrayList<>();
            for (Artist artist : Main.artistsList) {
                if (filters.getName() != null) {
                    if (!artist.getUsername().toLowerCase().
                            startsWith(filters.getName().toLowerCase())) {
                        continue;
                    }
                }

                // if we reached this point, it means that all the filters were respected
                result.add(artist);
            }

            // only keep the first 5 results of the search operation
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }

            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastArtistSearchResult(result);
                    user.setLastSearchTypeIndicator(TYPE_ARTIST);
                    user.setSearched(true);
                }
            }

            return result;
        }

        /**
         * Retrieves the names of artists from the given list of Artist objects.
         *
         * @param result the list of Artist objects.
         * @return an ArrayList of artist names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<Artist> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (Artist artist : result) {
                resultsNames.add(artist.getUsername());
            }
            return resultsNames;
        }
    }

    public class SearchCommandForHosts {
        /**
         * Searches for hosts in the given list based on specified filters.
         *
         * @return an ArrayList of Host objects that match the search criteria
         * (truncated to only max 5 elements).
         */
        public ArrayList<Host> executeSearch() {
            ArrayList<Host> result = new ArrayList<>();
            for (Host host : Main.hostsList) {
                if (filters.getName() != null) {
                    if (!host.getUsername().toLowerCase().
                            startsWith(filters.getName().toLowerCase())) {
                        continue;
                    }
                }

                // if we reached this point, it means that all the filters were respected
                result.add(host);
            }

            // only keep the first 5 results of the search operation
            if (result.size() > MAX_SEARCH_RESULTS) {
                for (int i = result.size() - 1; i >= MAX_SEARCH_RESULTS; i--) {
                    result.remove(i);
                }
            }

            for (NormalUser user : Main.normalUserList) {
                if (user.getUsername().equals(username)) {
                    user.setLastHostSearchResult(result);
                    user.setLastSearchTypeIndicator(TYPE_HOST);
                    user.setSearched(true);
                }
            }

            return result;
        }

        /**
         * Retrieves the names of hosts from the given list of Host objects.
         *
         * @param result the list of Host objects.
         * @return an ArrayList of host names (helper for printing results).
         */
        public ArrayList<String> getResultsNames(final ArrayList<Host> result) {
            ArrayList<String> resultsNames = new ArrayList<>();
            for (Host host : result) {
                resultsNames.add(host.getUsername());
            }
            return resultsNames;
        }
    }

}
