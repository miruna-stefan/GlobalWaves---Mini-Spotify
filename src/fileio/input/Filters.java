package fileio.input;

import java.util.ArrayList;

public class Filters {
    private String name;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private String releaseYear;
    private String artist;
    private String owner;
    private String description;
    private String username;

    /**
     * Gets the name filter.
     *
     * @return The name filter.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name filter.
     *
     * @param name The new name filter.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the album filter.
     *
     * @return The album filter.
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Sets the album filter.
     *
     * @param album The new album filter.
     */
    public void setAlbum(final String album) {
        this.album = album;
    }

    /**
     * Gets the tags filter.
     *
     * @return The tags filter.
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Sets the tags filter.
     *
     * @param tags The new tags filter.
     */
    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets the lyrics filter.
     *
     * @return The lyrics filter.
     */
    public String getLyrics() {
        return lyrics;
    }

    /**
     * Sets the lyrics filter.
     *
     * @param lyrics The new lyrics filter.
     */
    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    /**
     * Gets the genre filter.
     *
     * @return The genre filter.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre filter.
     *
     * @param genre The new genre filter.
     */
    public void setGenre(final String genre) {
        this.genre = genre;
    }

    /**
     * Gets the release year filter.
     *
     * @return The release year filter.
     */
    public String getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year filter.
     *
     * @param releaseYear The new release year filter.
     */
    public void setReleaseYear(final String releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets the artist filter.
     *
     * @return The artist filter.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist filter.
     *
     * @param artist The new artist filter.
     */
    public void setArtist(final String artist) {
        this.artist = artist;
    }

    /**
     * Gets the owner filter.
     *
     * @return The owner filter.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner filter.
     *
     * @param owner The new owner filter.
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Gets the description filter.
     *
     * @return The description filter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description filter.
     *
     * @param description The new description filter.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the username filter.
     *
     * @return The username filter.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username filter.
     *
     * @param username The new username filter.
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}
