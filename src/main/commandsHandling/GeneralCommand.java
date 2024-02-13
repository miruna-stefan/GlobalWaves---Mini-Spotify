package main.commandsHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import users.Artist;
import users.NormalUser;

public abstract class GeneralCommand implements Command {
    private static final int TYPE_SONG = 1;
    private static final int TYPE_PODCAST = 2;
    private static final int TYPE_PLAYLIST = 3;

    private static final int TYPE_ALBUM = 4;


    protected String command;
    protected Integer timestamp;
    protected ObjectNode node;

    public GeneralCommand(final String command, final Integer timestamp,
                          final ObjectNode node) {
        this.command = command;
        this.timestamp = timestamp;
        this.node = node;
    }

    /**
     * Gets the command string.
     *
     * @return the command string.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command string.
     *
     * @param command the command string to set.
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Gets the timestamp associated with the command.
     *
     * @return the timestamp.
     */
    public Integer getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp associated with the command.
     *
     * @param timestamp the timestamp to set.
     */
    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the JSON node associated with the command.
     *
     * @return the JSON node.
     */
    public ObjectNode getNode() {
        return node;
    }

    /**
     * Sets the JSON node associated with the command.
     *
     * @param node the JSON node to set.
     */
    public void setNode(final ObjectNode node) {
        this.node = node;
    }

    /**
     * Executes the command and returns the result as an ObjectNode.
     *
     * @return The result of the command as an ObjectNode.
     */
    public abstract ObjectNode execute();

    /**
     * Updates the last loaded entity's status based on the user's last loaded type indicator.
     *
     * @param user The normal user.
     */
    public void updateLastLoadedEntity(final NormalUser user) {
        // check if there is anything loaded
        if (!user.getLoaded()) {
            return;
        }

        if (user.getLastLoadTypeIndicator() == TYPE_SONG) {
            if (!user.getLastLoadedSongPlayInfo().getSongPaused()) {
                user.getLastLoadedSongPlayInfo().updateSongStatus(user, this.getTimestamp());
            }
            return;
        }

        if (user.getLastLoadTypeIndicator() == TYPE_PODCAST) {
            if (!user.getLastLoadedPodcast().getPodcastPaused()) {
                user.getLastLoadedPodcast().updatePodcastStatus(user, this.getTimestamp());
            }
            return;
        }

        if (user.getLastLoadTypeIndicator() == TYPE_PLAYLIST) {
            if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                if (!user.getLastLoadedPlaylist().getPaused()) {
                    user.getLastLoadedPlaylist().updatePlaylistStatus(user,
                            this.getTimestamp(), user.getLastLoadedPlaylist().getPlaylistSongs());
                }
            } else {
                if (!user.getLastLoadedPlaylist().getPaused()) {
                    user.getLastLoadedPlaylist().updatePlaylistStatus(user,
                            this.getTimestamp(), user.getLastLoadedPlaylist().
                                    getShuffledPlaylistSongs());
                }
            }

            return;
        }

        if (user.getLastLoadTypeIndicator() == TYPE_ALBUM) {
            if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                if (!user.getLastLoadedAlbum().getPaused()) {
                    user.getLastLoadedAlbum().updateAlbumStatus(user, this.getTimestamp(),
                            user.getLastLoadedAlbum().getSongs());
                }
            } else {
                if (!user.getLastLoadedAlbum().getPaused()) {
                    user.getLastLoadedAlbum().updateAlbumStatus(user, this.getTimestamp(),
                            user.getLastLoadedAlbum().getShuffledSongs());
                }
            }
            return;
        }
    }

    /**
     * Identifies the last loaded song based on the user's last loaded type indicator.
     *
     * @param user The normal user.
     * @return The identified SongPlayInfo.
     */
    public SongPlayInfo identifyLastLoadedSong(final NormalUser user) {
        SongPlayInfo currentSongPlayInfo = null;
        switch (user.getLastLoadTypeIndicator()) {
            case TYPE_SONG:
                currentSongPlayInfo = user.getLastLoadedSongPlayInfo();
                break;
            case TYPE_PLAYLIST:
                if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                    currentSongPlayInfo = user.getLastLoadedPlaylist().getPlaylistSongs().
                            get(user.getLastLoadedPlaylist().getCurrentSongIndex());
                } else {
                    currentSongPlayInfo = user.getLastLoadedPlaylist().getShuffledPlaylistSongs().
                            get(user.getLastLoadedPlaylist().getCurrentSongIndex());
                }
                break;
            case TYPE_ALBUM:
                if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                    currentSongPlayInfo = user.getLastLoadedAlbum().getSongs().
                            get(user.getLastLoadedAlbum().getCurrentSongIndex());
                } else {
                    currentSongPlayInfo = user.getLastLoadedAlbum().getShuffledSongs().
                            get(user.getLastLoadedAlbum().getCurrentSongIndex());
                }
                break;
            default:
                break;
        }
        return currentSongPlayInfo;
    }



    /**
     * Updates the statistics for the last loaded entity based on the
     * user's last loaded type indicator.
     *
     * @param user The normal user.
     */
    public void updateStats(final NormalUser user) {
        if (!user.getLoaded()) {
            return;
        }

        switch (user.getLastLoadTypeIndicator()) {
            case TYPE_SONG:
                if (!user.getLastLoadedSongPlayInfo().getSongPaused()) {
                    user.getLastLoadedSongPlayInfo().updateSongStatus(user, this.timestamp);
                }
                break;
            case TYPE_PODCAST:
                if (!user.getLastLoadedPodcast().getPodcastPaused()) {
                    user.getLastLoadedPodcast().updatePodcastStatus(user, this.timestamp);
                }
                break;
            case TYPE_PLAYLIST:
                if (!user.getLastLoadedPlaylist().getPaused()) {
                    if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                        user.getLastLoadedPlaylist().updatePlaylistStatus(user, this.timestamp,
                                user.getLastLoadedPlaylist().getPlaylistSongs());
                    } else {
                        user.getLastLoadedPlaylist().updatePlaylistStatus(user, this.timestamp,
                                user.getLastLoadedPlaylist().getShuffledPlaylistSongs());
                    }
                }
                break;
            case TYPE_ALBUM:
                if (!user.getLastLoadedAlbum().getPaused()) {
                    if (!user.getLastLoadedAlbum().getShuffleStatus()) {
                        user.getLastLoadedAlbum().updateAlbumStatus(user, this.timestamp,
                                user.getLastLoadedAlbum().getSongs());
                    } else {
                        user.getLastLoadedAlbum().updateAlbumStatus(user, this.timestamp,
                                user.getLastLoadedAlbum().getShuffledSongs());
                    }
                }
                break;
            default:
                break;
        }
    }


    /**
     * Updates the artist's status indicating if they had something on play.
     *
     * @param songPlayInfo The SongPlayInfo containing the song information.
     */
    public void updateArtistHadSomethingOnPlay(final SongPlayInfo songPlayInfo) {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(songPlayInfo.getSong().getArtist())) {
                artist.setHadSomethingOnPlay(true);
                break;
            }
        }
    }
}
