package fileio.input.Pagination;

import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongPlayInfo;
import users.NormalUser;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Page {
    // 0 - home; 1 - likedContentPage; 5 - artistPage; 6 - hostPage
    private int pageType;
    private static final int MAX_RESULTS = 5;

    public Page(final int pageType) {
        this.pageType = pageType;
    }

    /**
     * Gets the type of the page.
     *
     * @return The type of the page.
     */
    public int getPageType() {
        return pageType;
    }

    /**
     * Sets the type of the page.
     *
     * @param pageType The new type of the page.
     */
    public void setPageType(final int pageType) {
        this.pageType = pageType;
    }

    /**
     * Converts the Page object to its string representation.
     *
     * @return A formatted string representation of the Page.
     */
    public abstract String pageToString();

    /**
     * Retrieves the names of the top 5 playlists liked by a NormalUser.
     *
     * @param user the NormalUser for whom the top 5 playlists are retrieved.
     * @return an ArrayList of strings containing the names of the top 5 playlists.
     */
    public ArrayList<String> getTop5PlaylistsByLikes(final NormalUser user) {
        // update the total number of likes for each playlist followed by the user
        for (Playlist playlist : user.getFollowing()) {
            // check if the playlist is on shuffle
            if (!playlist.getShuffleStatus()) {
                playlist.updateNumberOfLikes(playlist.getPlaylistSongs());
            } else {
                playlist.updateNumberOfLikes(playlist.getShuffledPlaylistSongs());
            }
        }

        // sort the user's followed playlists by the NumberOf Likes in descending order
        ArrayList<Playlist> sortedPlaylists = new ArrayList<>();
        sortedPlaylists.addAll(user.getFollowing());
        sortedPlaylists.sort(new Comparator<Playlist>() {
            @Override
            public int compare(final Playlist playlist1, final Playlist playlist2) {
                return playlist2.getNumberOfLikes() - playlist1.getNumberOfLikes();
            }
        });

        // only keep the first 5 results of the search operation
        if (sortedPlaylists.size() > MAX_RESULTS) {
            for (int i = sortedPlaylists.size() - 1; i >= MAX_RESULTS; i--) {
                sortedPlaylists.remove(i);
            }
        }

        // create a list containing only the names of the playlists
        ArrayList<String> result = new ArrayList<>();
        for (Playlist playlist : sortedPlaylists) {
            result.add(playlist.getName());
        }

        return result;
    }

    /**
     * Retrieves the names of the top 5 liked songs for a NormalUser.
     *
     * @param user the NormalUser for whom the liked songs are retrieved.
     * @return an ArrayList of strings containing the names of the top 5 liked songs.
     */
    public ArrayList<String> getUsersTop5LikedSongNames(final NormalUser user) {
        ArrayList<SongPlayInfo> usersSortedSongs = new ArrayList<>();
        usersSortedSongs.addAll(user.getLikedSongs());

        // sort the user's liked songs by the NumberOf Likes in descending order
        usersSortedSongs.sort(new Comparator<SongPlayInfo>() {
            @Override
            public int compare(final SongPlayInfo song1, final SongPlayInfo song2) {
                return song2.getNumberOfLikes() - song1.getNumberOfLikes();
            }
        });

        // only keep the first 5 results of the search operation
        if (usersSortedSongs.size() > MAX_RESULTS) {
            for (int i = usersSortedSongs.size() - 1; i >= MAX_RESULTS; i--) {
                usersSortedSongs.remove(i);
            }
        }

        // create a list containing only the names of the songs
        ArrayList<String> result = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : usersSortedSongs) {
            result.add(songPlayInfo.getSong().getName());
        }

        return result;
    }

    /**
     * Retrieves and formats the names of liked songs for a NormalUser.
     *
     * @param user the NormalUser for whom liked songs are retrieved.
     * @return an ArrayList of strings containing the names of liked songs.
     */
    public ArrayList<String> getLikedSongsString(final NormalUser user) {
        ArrayList<String> result = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : user.getLikedSongs()) {
            String likedSongString = songPlayInfo.getSong().getName()
                    + " - " + songPlayInfo.getSong().getArtist();
            result.add(likedSongString);
        }
        return result;
    }

    /**
     * Retrieves and formats the names of followed playlists for a NormalUser.
     *
     * @param user the NormalUser for whom followed playlists are retrieved.
     * @return an ArrayList of strings containing the names of followed playlists.
     */
    public ArrayList<String> getFollowingPlaylistsString(final NormalUser user) {
        ArrayList<String> result = new ArrayList<>();
        for (Playlist playlist : user.getFollowing()) {
            String playlistString = playlist.getName() + " - " + playlist.getOwner();
            result.add(playlistString);
        }
        return result;
    }
}
