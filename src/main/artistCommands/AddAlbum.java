package main.artistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.Album;
import fileio.input.audioEntities.SongPlayInfo;
import main.Main;
import users.Artist;

import java.util.ArrayList;

public final class AddAlbum extends StandardArtistCommand {
    private Integer releaseYear;
    private String description;
    private ArrayList<SongPlayInfo> songs;

    // Singleton instance field
    private static AddAlbum instance = null;

    private AddAlbum(final String command, final String username, final Integer timestamp,
                     final String name, final Integer releaseYear, final String description,
                    final ArrayList<SongPlayInfo> songs, final ObjectNode node) {
        super(command, username, timestamp, name, node);
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
    }

    /**
     * Gets the singleton instance of AddAlbum.
     *
     * @param command      the command string indicating the type of command.
     * @param username     the username associated with the command.
     * @param timestamp    the timestamp of the command.
     * @param name         the name of the album to be added.
     * @param releaseYear  the release year of the album.
     * @param description  the description of the album.
     * @param songs        the list of songs associated with the album.
     * @param node         the JSON node associated with the command.
     * @return the singleton instance of AddAlbum.
     */
    public static AddAlbum getInstance(final String command, final String username,
                                       final Integer timestamp, final String name,
                                       final Integer releaseYear, final String description,
                                       final ArrayList<SongPlayInfo> songs, final ObjectNode node) {
        if (instance == null) {
            instance = new AddAlbum(command, username, timestamp,
                    name, releaseYear, description, songs, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setReleaseYear(releaseYear);
            instance.setDescription(description);
            instance.setSongs(songs);
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
     * Checks if the list of songs has duplicates.
     *
     * @param songList the list of songs to be checked.
     * @return true if there are duplicate songs, false otherwise.
     */
    private Boolean hasDuplicates(final ArrayList<SongPlayInfo> songList) {
        for (int i = 0; i < songList.size(); i++) {
            for (int j = i + 1; j < songList.size(); j++) {
                if (songList.get(i).getSong().getName().equals(songList
                        .get(j).getSong().getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Executes the addition of a new album by the artist.
     *
     * @return a message indicating the success or failure of the addition operation.
     */
    public String getCommandMessage() {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // this artist has already been created => the command can be executed

                // check if the album name is already taken
                for (Album album : artist.getAlbums()) {
                    if (album.getName().equals(this.getName())) {
                        return this.getUsername() + " has another album with the same name.";
                    }
                }

                // check song list validity
                if (hasDuplicates(this.songs)) {
                    return this.getUsername() + " has the same song at least twice in this album.";
                }

                // if the songlist is valid, create the album
                Album newAlbum = new Album(this.getName(), this.releaseYear,
                        this.description, this.songs);
                artist.getAlbums().add(newAlbum);

                // add the songs from the new album in the big songList (containing all songs)
                Main.songsList.addAll(this.songs);

                // prepare the notification message
                ObjectNode newNotification = JsonNodeFactory.instance.objectNode();
                newNotification.put("name", "New Album");
                newNotification.put("description", "New Album from " + artist.getUsername() + ".");
                artist.notifyObservers(newNotification);

                return this.getUsername() + " has added new album successfully.";
            }
        }

        // if we reached this point, it means that the username doesn't appear in the artist list
        return checkUsernameExistence();
    }


    /**
     * Gets the release year of the album.
     *
     * @return the release year.
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year of the album.
     *
     * @param releaseYear the release year to be set.
     */
    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets the description of the album.
     *
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the album.
     *
     * @param description the description to be set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the list of songs associated with the album.
     *
     * @return the list of songs.
     */
    public ArrayList<SongPlayInfo> getSongs() {
        return songs;
    }

    /**
     * Sets the list of songs associated with the album.
     *
     * @param songs the list of songs to be set.
     */
    public void setSongs(final ArrayList<SongPlayInfo> songs) {
        this.songs = songs;
    }

    /**
     * Gets the singleton instance of AddAlbum.
     *
     * @return the singleton instance of AddAlbum.
     */
    public static AddAlbum getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of AddAlbum.
     *
     * @param instance the instance to be set.
     */
    public static void setInstance(final AddAlbum instance) {
        AddAlbum.instance = instance;
    }
}
