package fileio.input.Pagination;

import users.NormalUser;

import java.util.ArrayList;

public class HomePage extends Page {
    private NormalUser user;


    public HomePage(final NormalUser user) {
        super(0);
        this.user = user;
    }

    /**
     * Retrieves the names of the recommended playlists for the user.
     *
     * @return An ArrayList of strings containing the names of recommended playlists.
     */
    public ArrayList<String> getPlaylistsNamesList() {
        ArrayList<String> playListsNames = new ArrayList<>();
        if (user.getRandomPlaylistRecommendation() != null) {
            playListsNames.add(user.getRandomPlaylistRecommendation().getName());
        }

        if (user.getFansPlaylistRecommendations() != null) {
            playListsNames.add(user.getFansPlaylistRecommendations().getName());
        }

        return playListsNames;
    }

    /**
     * Converts the HomePage object to its string representation.
     *
     * @return A formatted string representation of the HomePage.
     */
    @Override
    public String pageToString() {
        return "Liked songs:\n\t" + getUsersTop5LikedSongNames(user)
                + "\n\nFollowed playlists:\n\t" + getTop5PlaylistsByLikes(user)
                + "\n\nSong recommendations:\n\t" + user.getSongRecommendations()
                + "\n\nPlaylists recommendations:\n\t" + getPlaylistsNamesList();
    }

    /**
     * Gets the NormalUser for whom this home page is created.
     *
     * @return The NormalUser for whom this home page is created.
     */
    public NormalUser getUser() {
        return user;
    }

    /**
     * Sets the NormalUser for whom this home page is created.
     *
     * @param user The new NormalUser for whom this home page is created.
     */
    public void setUser(final NormalUser user) {
        this.user = user;
    }
}
