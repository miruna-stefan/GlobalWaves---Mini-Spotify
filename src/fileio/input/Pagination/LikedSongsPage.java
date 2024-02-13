package fileio.input.Pagination;

import users.NormalUser;

public class LikedSongsPage extends Page {
    private NormalUser user;

    public LikedSongsPage(final NormalUser user) {
        super(1);
        this.user = user;
    }

    /**
     * Converts the LikedSongsPage object to its string representation.
     *
     * @return A formatted string representation of the LikedSongsPage.
     */
    @Override
    public String pageToString() {
        return "Liked songs:\n\t" + getLikedSongsString(user)
                + "\n\nFollowed playlists:\n\t" + getFollowingPlaylistsString(user);
    }

    /**
     * Gets the NormalUser who owns this LikedSongsPage.
     *
     * @return The NormalUser who owns this LikedSongsPage.
     */
    public NormalUser getUser() {
        return user;
    }

    /**
     * Sets the NormalUser who owns this LikedSongsPage.
     *
     * @param user The new NormalUser who owns this LikedSongsPage.
     */
    public void setUser(final NormalUser user) {
        this.user = user;
    }
}
