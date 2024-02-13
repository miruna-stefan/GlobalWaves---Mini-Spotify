package main.adminCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.ArtistPage;
import fileio.input.Pagination.HostPage;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.PodcastInput;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;

public final class DeleteUser extends StandardAdminCommand {
    // Singleton instance field
    private static DeleteUser instance = null;

    private DeleteUser(final String command, final String username,
                       final Integer timestamp, final ObjectNode node) {
        super(command, username, timestamp, node);
    }

    /**
     * Gets the singleton instance of DeleteUser.
     *
     * @param command   The command string.
     * @param username  The username of the user to be deleted.
     * @param timestamp The timestamp of the deletion command.
     * @param node      The JSON node associated with the command.
     * @return The DeleteUser instance.
     */
    public static DeleteUser getInstance(final String command, final String username,
                                         final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new DeleteUser(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Executes the delete user command.
     *
     * @return A message indicating the success or failure of the deletion.
     */
    public String getCommandMessage() {
        // search for the username and identify the type of the user
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                // the given username belongs to a normal user

                // check if the user can be deleted
                DeletionExecutionAccordingToUserType deletionExecution;
                deletionExecution = new DeletionExecutionAccordingToUserType(this.timestamp);
                if (!user.acceptDeletion(deletionExecution)) {
                    return this.getUsername() + " can't be deleted.";
                }

                // if we reached this point, it means that the user can be deleted
                Main.normalUserList.remove(user);
                return this.getUsername() + " was successfully deleted.";
            }
        }

        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // the given username belongs to an artist

                // check if the artist can be deleted
                DeletionExecutionAccordingToUserType deletionExecution;
                deletionExecution = new DeletionExecutionAccordingToUserType(this.timestamp);
                if (!artist.acceptDeletion(deletionExecution)) {
                    return this.getUsername() + " can't be deleted.";
                }

                // if we reached this point, it means that the artist can be deleted
                Main.artistsList.remove(artist);
                return this.getUsername() + " was successfully deleted.";
            }
        }

        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.getUsername())) {
                // the given username belongs to a host

                // check if the host can be deleted
                DeletionExecutionAccordingToUserType deletionExecution;
                deletionExecution = new DeletionExecutionAccordingToUserType(this.timestamp);
                if (!host.acceptDeletion(deletionExecution)) {
                    return this.getUsername() + " can't be deleted.";
                }

                // if we reached this point, it means that the host can be deleted
                Main.hostsList.remove(host);
                return this.getUsername() + " was successfully deleted.";
            }
        }

        // if we reached this point, it means that the username does not exist
        return "The username " + this.getUsername() + " doesn't exist.";
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getCommandMessage());
        return node;
    }


    /**
     * Represents the logic for deletion based on the type of user.
     */
    public class DeletionExecutionAccordingToUserType implements VisitorDeletion {
        private static final int TYPE_PLAYLIST = 3;
        private static final int TYPE_ARTIST = 5;
        private static final int TYPE_HOST = 6;
        private Integer timestamp;

        /**
         * Constructs a DeletionExecutionAccordingToUserType instance.
         *
         * @param timestamp The timestamp of the deletion command.
         */
        public DeletionExecutionAccordingToUserType(final Integer timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Checks if a normal user can be deleted.
         *
         * @param normalUser The normal user to be deleted.
         * @return True if the user can be deleted, false otherwise.
         */
        @Override
        public Boolean canBeDeleted(final NormalUser normalUser) {
            // check if another user has loaded a playlist created by this user
            for (NormalUser user : Main.normalUserList) {
                // check if the user has anything loaded
                if (!user.getLoaded()) {
                    continue;
                }

                // check if the last search command was for a playlist
                if (user.getLastSearchTypeIndicator() != TYPE_PLAYLIST) {
                    continue;
                }

                if (user.getLastLoadedPlaylist() == null) {
                    continue;
                }
                // update status of the playlist loaded in player if it's not on pause
                if (!user.getLastLoadedPlaylist().getPaused()) {
                    if (!user.getLastLoadedPlaylist().getShuffleStatus()) {
                        user.getLastLoadedPlaylist().updatePlaylistStatus(user, this.timestamp,
                                user.getLastLoadedPlaylist().getPlaylistSongs());
                    } else {
                        user.getLastLoadedPlaylist().updatePlaylistStatus(user, this.timestamp,
                                user.getLastLoadedPlaylist().getShuffledPlaylistSongs());
                    }
                    // check if the playlist has finished meanwhile
                    if (user.getLastLoadedPlaylist() == null) {
                        continue;
                    }
                    user.getLastLoadedPlaylist().setLastPlayTimestamp(this.timestamp);
                }

                if (user.getLastLoadedPlaylist().getOwner().equals(normalUser.getUsername())) {
                    return false;
                }
            }

            /* if we reached this point, it means that we haven't found any user that has
            loaded a playlist created by this user */
            prepareForDeletion(normalUser);
            return true;
        }

        /**
         * Prepares for deletion of a normal user.
         *
         * @param normalUser The normal user to be deleted.
         */
        public void prepareForDeletion(final NormalUser normalUser) {
            // remove all the user's playlists from other users' following lists
            for (Playlist playlist : normalUser.getPlaylists()) {
                for (NormalUser user : Main.normalUserList) {
                    if (!user.getUsername().equals(normalUser.getUsername())
                            && user.getFollowing().contains(playlist)) {
                        user.getFollowing().remove(playlist);
                    }
                }
            }

            // remove likes from songs liked by this user
            for (SongPlayInfo song : normalUser.getLikedSongs()) {
                song.setNumberOfLikes(song.getNumberOfLikes() - 1);
            }

            // remove follow from playlists followed by the user
            for (Playlist followedPlaylist : normalUser.getFollowing()) {
                followedPlaylist.setFollowers(followedPlaylist.getFollowers() - 1);
            }

            // if any user has selected a playlist owned by this user, delete selection
            for (NormalUser user : Main.normalUserList) {
                if (!user.getSearched()) {
                    continue;
                }
                if (user.getLastSearchTypeIndicator() == TYPE_PLAYLIST
                        && user.getSelected()) {
                    if (user.getLastSelectedPlaylist().getOwner()
                            .equals(normalUser.getUsername())) {
                        user.setSelected(false);
                        user.setLastSelectedPlaylist(null);
                    }
                }
            }
        }

        /**
         * Checks if an artist can be deleted.
         *
         * @param artist The artist to be deleted.
         * @return True if the artist can be deleted, false otherwise.
         */
        @Override
        public Boolean canBeDeleted(final Artist artist) {
            // check if any user is currently on the host's page
            for (NormalUser user : Main.normalUserList) {
                if (user.getPageHistory().get(user.getCurrentPageIndex()).
                        getPageType() == TYPE_ARTIST) {
                    ArtistPage artistPage = (ArtistPage) user.getPageHistory().
                            get(user.getCurrentPageIndex());
                    if (artistPage.getArtistPageOwner().getUsername().
                            equals(artist.getUsername())) {
                        return false;
                    }
                }
            }

            // check if all the artist's albums can be deleted
            for (Album album : artist.getAlbums()) {
                if (!artist.canDeleteAlbum(album, this.timestamp)) {
                    return false;
                }
            }

            /* if we reached this point, it means that we haven't
            found any user that has loaded a playlist created
            by this user => the artist can be deleted */
            prepareForDeletion(artist);
            return true;
        }

        /**
         * Prepares for deletion of an artist.
         *
         * @param artist The artist to be deleted.
         */
        public void prepareForDeletion(final Artist artist) {
            /* eliminate all the songs in the artist's albums
            from the big list of songs, the other users' playlists
            and lists of liked songs */
            for (Album album : artist.getAlbums()) {
                artist.prepareAlbumRemoval(album);
            }
        }

        /**
         * Checks if a host can be deleted.
         *
         * @param host The host to be deleted.
         * @return True if the host can be deleted, false otherwise.
         */
        @Override
        public Boolean canBeDeleted(final Host host) {
            // check if any user is currently on the host's page
            for (NormalUser user : Main.normalUserList) {
                if (user.getPageHistory().get(user.getCurrentPageIndex()).
                        getPageType() == TYPE_HOST) {
                    HostPage hostPage = (HostPage) user.getPageHistory().
                            get(user.getCurrentPageIndex());
                    if (hostPage.getHostPageOwner().getUsername()
                            .equals(host.getUsername())) {
                        return false;
                    }
                }
            }

            // check if all of the host's podcasts can be deleted
            for (PodcastInput podcast : host.getPodcasts()) {
                if (!host.canDeletePodcast(podcast, this.timestamp)) {
                    return false;
                }
            }
            prepareForDeletion(host);
            return true;
        }

        /**
         * Prepares for deletion of a host.
         *
         * @param host The host to be deleted.
         */
        public void prepareForDeletion(final Host host) {
            for (PodcastInput podcast : host.getPodcasts()) {
                host.preparePodcastRemoval(podcast);
            }

            // remove all the host's podcasts from the big list of podcasts
            for (PodcastInput podcast : host.getPodcasts()) {
                Main.podcastsList.remove(podcast);
            }
        }


        /**
         * Gets the timestamp of the deletion command.
         *
         * @return The timestamp.
         */
        public Integer getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the timestamp of the deletion command.
         *
         * @param timestamp The timestamp to set.
         */
        public void setTimestamp(final Integer timestamp) {
            this.timestamp = timestamp;
        }
    }



}
