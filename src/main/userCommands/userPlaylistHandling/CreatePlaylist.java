package main.userCommands.userPlaylistHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import fileio.input.audioEntities.Playlist;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class CreatePlaylist extends StandardCommandForUserPlayer {
    private String playlistName;
    private String owner;
    private ArrayList<Playlist> allUsersList;

    // Singleton instance field
    private static CreatePlaylist instance = null;

    /**
     * Constructs a new CreatePlaylist with the specified parameters.
     *
     * @param username      the username associated with the command.
     * @param timestamp     the timestamp of the command.
     * @param playlistName  the name of the playlist to be created.
     * @param node          the JSON node associated with the command.
     * @param allUsersList  the list of all playlists from all users.
     */
    // make constructor private for swingleton design pattern
    private CreatePlaylist(final String command, final String username, final Integer timestamp,
                          final String playlistName, final ObjectNode node,
                          final ArrayList<Playlist> allUsersList) {
        super(command, timestamp, node, username);
        this.playlistName = playlistName;
        this.owner = username;
        this.allUsersList = allUsersList;
    }

    /**
     * Gets the singleton instance of CreatePlaylist.
     *
     * @param username      the username associated with the command.
     * @param timestamp     the timestamp of the command.
     * @param playlistName  the name of the playlist to be created.
     * @param node          the JSON node associated with the command.
     * @param allUsersList  the list of all playlists from all users.
     * @return the singleton instance of CreatePlaylist.
     */
    public static CreatePlaylist getInstance(final String command, final String username,
                                             final Integer timestamp,
                                             final String playlistName, final ObjectNode node,
                                             final ArrayList<Playlist> allUsersList) {
        if (instance == null) {
            instance = new CreatePlaylist(command, username, timestamp, playlistName,
                    node, allUsersList);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setPlaylistName(playlistName);
            instance.setNode(node);
            instance.setOwner(username);
            instance.allUsersList = allUsersList;
        }
        return instance;
    }

    /**
     * Prints the result of the playlist creation.
     *
     * @return The ObjectNode containing information about the playlist creation.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            return node;
        }
        node.put("message", this.executeCreatePlaylist(this.getAllUsersList()));
        return node;
    }


    /**
     * Gets the username of the user who created the playlist.
     *
     * @return The owner of the playlist.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the playlist.
     *
     * @param owner The new owner of the playlist.
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Gets the name of the playlist.
     *
     * @return The name of the playlist.
     */
    public String getPlaylistName() {
        return playlistName;
    }

    /**
     * Sets the name of the playlist.
     *
     * @param playlistName The new name of the playlist.
     */
    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public ArrayList<Playlist> getAllUsersList() {
        return allUsersList;
    }

    public void setAllUsersList(final ArrayList<Playlist> allUsersList) {
        this.allUsersList = allUsersList;
    }

    /**
     * Executes the command to create a playlist.
     *
     * @param allUsersPlaylists The list of all playlists from all users.
     * @return A message indicating the result of the playlist creation.
     */
    public String executeCreatePlaylist(final ArrayList<Playlist> allUsersPlaylists) {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                // check if there is already a playlist with the same name
                if (isThisNameTaken(user)) {
                    return "A playlist with the same name already exists.";
                }

                // create a new playlist
                Playlist newPlaylist = new Playlist(this.playlistName, false, this.username);
                user.getPlaylists().add(newPlaylist);
                allUsersPlaylists.add(newPlaylist);
            }
        }
        return "Playlist created successfully.";
    }

    /**
     * Checks if a playlist with the same name already exists for the given user.
     *
     * @param user The user for whom the check is performed.
     * @return True if a playlist with the same name already exists, false otherwise.
     */
    public Boolean isThisNameTaken(final NormalUser user) {
        if (!user.getPlaylists().isEmpty()) {
            for (Playlist playlist : user.getPlaylists()) {
                if (playlist.getName().equals(this.playlistName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
