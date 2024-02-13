package main.artistCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import main.Main;
import users.Artist;

public final class RemoveAlbum extends StandardArtistCommand {

    // Singleton instance field
    private static RemoveAlbum instance = null;

    private RemoveAlbum(final String command, final String username, final Integer timestamp,
                        final String name, final ObjectNode node) {
        super(command, username, timestamp, name, node);
    }

    /**
     * Gets the singleton instance of RemoveAlbum.
     *
     * @param command   the command string indicating the type of command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the album to be removed.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of RemoveAlbum.
     */
    public static RemoveAlbum getInstance(final String command, final String username,
                                          final Integer timestamp, final String name,
                                          final ObjectNode node) {
        if (instance == null) {
            instance = new RemoveAlbum(command, username, timestamp, name, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
        }
        return instance;
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
     * Executes the removal of the specified album by the artist.
     *
     * @return a message indicating the success or failure of the removal operation.
     */
    public String getCommandMessage() {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // this artist has already been created => the command can be executed

                // check if the artist has an album with the given name
                Album albumToRemove = null;
                for (Album album : artist.getAlbums()) {
                    if (album.getName().equals(this.getName())) {
                        albumToRemove = album;
                        break;
                    }
                }
                if (albumToRemove == null) {
                    return this.getUsername() + " doesn't have an album with the given name.";
                }

                // check if the album can be deleted
                if (!artist.canDeleteAlbum(albumToRemove, this.timestamp)) {
                    return this.getUsername() + " can't delete this album.";
                }

                // if we are here, it means that we need to remove this album
                artist.prepareAlbumRemoval(albumToRemove);
                // remove album from this artist's list of albums
                for (Album album : artist.getAlbums()) {
                    if (album.getName().equals(this.getName())) {
                        artist.getAlbums().remove(album);
                        return artist.getUsername() + " deleted the album successfully.";
                    }
                }
            }
        }

        // if we reached this point, it means that the username doesn't appear in the artist list
        return checkUsernameExistence();
    }

}
