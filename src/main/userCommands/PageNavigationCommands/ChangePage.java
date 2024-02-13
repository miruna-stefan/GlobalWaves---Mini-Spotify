package main.userCommands.PageNavigationCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.Page;
import fileio.input.Pagination.HomePage;
import fileio.input.Pagination.LikedSongsPage;
import fileio.input.Pagination.ArtistPage;
import fileio.input.Pagination.HostPage;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.Artist;
import users.Host;
import users.NormalUser;

public final class ChangePage extends StandardCommandForUserPlayer {
    private String nextPage;

    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;

    // Singleton instance field
    private static ChangePage instance = null;

    private ChangePage(final String command, final String username, final Integer timestamp,
                       final ObjectNode node, final String nextPage) {
        super(command, timestamp, node, username);
        this.nextPage = nextPage;
    }

    /**
     * Creates a new ChangePage instance.
     *
     * @param command   the command name
     * @param username  the username of the user that issued the command
     * @param timestamp the timestamp when the command was issued
     * @param node      the parameters of the command
     * @param nextPage  the identifier of the next page
     * @return a new ChangePage instance
     */
    public static ChangePage getInstance(final String command, final String username,
                                         final Integer timestamp, final ObjectNode node,
                                         final String nextPage) {
        if (instance == null) {
            instance = new ChangePage(command, username, timestamp, node, nextPage);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
            instance.setNextPage(nextPage);
        }
        return instance;
    }

    /**
     * Prints the result of the change page command.
     *
     * @return The ObjectNode containing information about the change page command.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            return node;
        }
        node.put("message", this.executeChangePage());
        return node;
    }

    /**
     * Checks if the next page is valid.
     *
     * @return true if the next page is valid, false otherwise
     */
    public Boolean checkNextPageValidity() {
        if (this.getNextPage().equals("Home")
                || this.getNextPage().equals("LikedContent")
                || this.getNextPage().equals("Artist")
                || this.getNextPage().equals("Host")) {
            return true;
        }
        return false;
    }


    /**
     * Executes the change page command.
     *
     * @return the result of the command
     */
    public String executeChangePage() {
        // change next page validity
        if (!checkNextPageValidity()) {
            return this.username + " is trying to access a non-existent page.";
        }

        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                switch (this.getNextPage()) {
                    case "Home":
                        Page homePage = new HomePage(user);
                        user.getPageHistory().add(homePage);
                        break;
                    case "LikedContent":
                        Page likedSongsPage = new LikedSongsPage(user);
                        user.getPageHistory().add(likedSongsPage);
                        break;
                    case "Artist":
                        // get the artist of the last loaded audiofile
                        if (user.getLastLoadTypeIndicator() == TYPE_PODCAST) {
                            return this.username + " is trying to access a non-existent page.";
                        }

                        Artist artistPageOwner = null;
                        switch (user.getLastLoadTypeIndicator()) {
                            case TYPE_SONG:
                                for (Artist artist : Main.artistsList) {
                                    if (artist.getUsername().equals(user.
                                            getLastLoadedSongPlayInfo().getSong().getArtist())) {
                                        artistPageOwner = artist;
                                        break;
                                    }
                                }
                                break;
                            case TYPE_ALBUM:
                                for (Artist artist : Main.artistsList) {
                                    if (artist.getUsername().equals(user.getLastLoadedAlbum().
                                            getSongs().get(0).getSong().getArtist())) {
                                        artistPageOwner = artist;
                                        break;
                                    }
                                }
                                break;
                            case TYPE_PLAYLIST:
                                for (Artist artist : Main.artistsList) {
                                    if (artist.getUsername().equals(user.getLastLoadedPlaylist().
                                            getPlaylistSongs().get(user.getLastLoadedPlaylist().
                                                    getCurrentSongIndex()).getSong().getArtist())) {
                                        artistPageOwner = artist;
                                        break;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        if (artistPageOwner == null) {
                            return this.username + " is trying to access a non-existent page.";
                        }

                        Page artistPage = new ArtistPage(artistPageOwner);
                        user.getPageHistory().add(artistPage);
                        break;
                    case "Host":
                        // get the host of the last loaded audiofile
                        if (user.getLastLoadTypeIndicator() != TYPE_PODCAST) {
                            return this.username + " is trying to access a non-existent page.";
                        }
                        for (Host host : Main.hostsList) {
                            if (host.getUsername().equals(user.getLastLoadedPodcast().
                                    getPodcast().getOwner())) {
                                Page hostPage = new HostPage(host);
                                user.getPageHistory().add(hostPage);
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
                user.setCurrentPageIndex(user.getPageHistory().size() - 1);
            }
        }
        return this.username + " accessed " + this.getNextPage() + " successfully.";
    }

    /**
     * Gets the identifier of the next page.
     *
     * @return a String representing the identifier of the next page.
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * Sets the identifier of the next page.
     *
     * @param nextPage a String representing the identifier of the next page to be set.
     */
    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }
}
