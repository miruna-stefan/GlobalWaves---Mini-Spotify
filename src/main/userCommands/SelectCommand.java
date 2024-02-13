package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.ArtistPage;
import fileio.input.Pagination.HostPage;
import fileio.input.Pagination.Page;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.PodcastInput;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;

public final class SelectCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;
    private static final int TYPE_ALBUM = 4;
    private static final int TYPE_ARTIST = 5;
    private static final int TYPE_HOST = 6;
    private Integer itemNumber;

    /**
     * Retrieves the item number to be selected.
     *
     * @return the item number to be selected.
     */
    public Integer getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the item number to be selected.
     *
     * @param itemNumber the item number to be selected.
     */
    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    // Singleton instance field
    private static SelectCommand instance = null;

    /**
     * Gets the singleton instance of SelectCommand.
     *
     * @return the singleton instance of SelectCommand.
     */
    public static SelectCommand getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of SelectCommand.
     *
     * @param instance the singleton instance of SelectCommand.
     */
    public static void setInstance(final SelectCommand instance) {
        SelectCommand.instance = instance;
    }

    /**
     * Constructs a new SelectCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param itemNumber the item number to be selected.
     * @param node      the JSON node associated with the command.
     */

    // make constructor private for the singleton design pattern
    private SelectCommand(final String command, final String username, final Integer timestamp,
                          final Integer itemNumber, final ObjectNode node) {
        super(command, timestamp, node, username);
        this.itemNumber = itemNumber;
    }

    /**
     * Gets the singleton instance of SelectCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param itemNumber the item number to be selected.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of SelectCommand.
     */
    public static SelectCommand getInstance(final String command, final String username,
                                            final Integer timestamp,
                                            final Integer itemNumber, final ObjectNode node) {
        if (instance == null) {
            instance = new SelectCommand(command, username, timestamp, itemNumber, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setItemNumber(itemNumber);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Generates a JSON node containing the results of the select command.
     *
     * @return the JSON node representing the select command results.
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
     * Executes the select command based on the last search type and selected item number.
     *
     * @return a String message indicating the result of the selection.
     */
    public String getCommandMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.getSearched()) {
                    return "Please conduct a search before making a selection.";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_SONG) {
                    // the last search command type was song
                    if (user.getLastSongSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    SongPlayInfo result;
                    result = user.getLastSongSearchResult().get(this.itemNumber - 1);
                    user.setLastSelectedSong(result);
                    return "Successfully selected " + user.getLastSelectedSong()
                            .getSong().getName() + ".";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                    // the last search command type was podcast
                    if (user.getLastPodcastSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    PodcastInput result;
                    result = user.getLastPodcastSearchResult().get(this.itemNumber - 1);
                    user.setLastSelectedPodcast(result);
                    return "Successfully selected " + user.getLastSelectedPodcast().getName() + ".";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST) {
                    // the last search command type was podcast
                    if (user.getLastPlaylistSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    Playlist result;
                    result = user.getLastPlaylistSearchResult().get(this.itemNumber - 1);
                    user.setLastSelectedPlaylist(result);
                    String resultName = user.getLastSelectedPlaylist().getName();
                    return "Successfully selected " + resultName + ".";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ALBUM) {
                    // the last search command type was album
                    if (user.getLastAlbumSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    Album result;
                    result = user.getLastAlbumSearchResult().get(this.itemNumber - 1);
                    user.setLastSelectedAlbum(result);
                    String resultName = user.getLastSelectedAlbum().getName();
                    return "Successfully selected " + resultName + ".";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_ARTIST) {
                    // the last search command type was artist
                    if (user.getLastArtistSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    Artist selectedArtist = user.getLastArtistSearchResult()
                            .get(this.itemNumber - 1);
                    //change the user's page to the artist's page
                    Page artistPage = new ArtistPage(selectedArtist);
                    user.getPageHistory().add(artistPage);
                    user.setCurrentPageIndex(user.getPageHistory().size() - 1);

                    return "Successfully selected " + selectedArtist.getUsername() + "'s page.";
                }
                if (user.getLastSearchTypeIndicator() == TYPE_HOST) {
                    // the last search command type was host
                    if (user.getLastHostSearchResult().size() < this.itemNumber) {
                        return "The selected ID is too high.";
                    }
                    user.setSelected(true);
                    Host selectedHost = user.getLastHostSearchResult().get(this.itemNumber - 1);
                    //change the user's page to the host's page
                    Page hostPage = new HostPage(selectedHost);
                    user.getPageHistory().add(hostPage);
                    user.setCurrentPageIndex(user.getPageHistory().size() - 1);

                    return "Successfully selected " + selectedHost.getUsername() + "'s page.";
                }
            }
        }

        // add check for playlists
        return "Please conduct a search before making a selection.";
    }
}

