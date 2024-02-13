package fileio.input;

import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.SongInput;

import java.util.ArrayList;

public class InputCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private String type;
    private Filters filters;
    private Integer itemNumber;
    private String playlistName;
    private Integer playlistId;
    private Integer seed;
    private Integer age;
    private String city;
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<SongInput> songs;
    private String date;
    private Integer price;
    private ArrayList<EpisodeInput> episodes;
    private String nextPage;
    private String recommendationType;

    /**
     * Gets the command associated with this input.
     *
     * @return The command associated with this input.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command associated with this input.
     *
     * @param command The new command associated with this input.
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Gets the username associated with this input.
     *
     * @return The username associated with this input.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with this input.
     *
     * @param username The new username associated with this input.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the timestamp associated with this input.
     *
     * @return The timestamp associated with this input.
     */
    public Integer getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp associated with this input.
     *
     * @param timestamp The new timestamp associated with this input.
     */
    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type associated with this input.
     *
     * @return The type associated with this input.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type associated with this input.
     *
     * @param type The new type associated with this input.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the filters associated with this input.
     *
     * @return The filters associated with this input.
     */
    public Filters getFilters() {
        return filters;
    }

    /**
     * Sets the filters associated with this input.
     *
     * @param filters The new filters associated with this input.
     */
    public void setFilters(final Filters filters) {
        this.filters = filters;
    }

    /**
     * Gets the item number associated with this input.
     *
     * @return The item number associated with this input.
     */
    public Integer getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the item number associated with this input.
     *
     * @param itemNumber The new item number associated with this input.
     */
    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Gets the playlist name associated with this input.
     *
     * @return The playlist name associated with this input.
     */
    public String getPlaylistName() {
        return playlistName;
    }

    /**
     * Sets the playlist name associated with this input.
     *
     * @param playlistName The new playlist name associated with this input.
     */
    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    /**
     * Gets the playlist ID associated with this input.
     *
     * @return The playlist ID associated with this input.
     */
    public Integer getPlaylistId() {
        return playlistId;
    }

    /**
     * Sets the playlist ID associated with this input.
     *
     * @param playlistId The new playlist ID associated with this input.
     */
    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Gets the seed associated with this input.
     *
     * @return The seed associated with this input.
     */
    public Integer getSeed() {
        return seed;
    }

    /**
     * Sets the seed associated with this input.
     *
     * @param seed The new seed associated with this input.
     */
    public void setSeed(final Integer seed) {
        this.seed = seed;
    }

    /**
     * Gets the age associated with this input.
     *
     * @return The age associated with this input.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age associated with this input.
     *
     * @param age The new age associated with this input.
     */
    public void setAge(final Integer age) {
        this.age = age;
    }

    /**
     * Gets the city associated with this input.
     *
     * @return The city associated with this input.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city associated with this input.
     *
     * @param city The new city associated with this input.
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Gets the name associated with this input.
     *
     * @return The name associated with this input.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with this input.
     *
     * @param name The new name associated with this input.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the release year associated with this input.
     *
     * @return The release year associated with this input.
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year associated with this input.
     *
     * @param releaseYear The new release year associated with this input.
     */
    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets the description associated with this input.
     *
     * @return The description associated with this input.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description associated with this input.
     *
     * @param description The new description associated with this input.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the songs associated with this input.
     *
     * @return The songs associated with this input.
     */
    public ArrayList<SongInput> getSongs() {
        return songs;
    }

    /**
     * Sets the songs associated with this input.
     *
     * @param songs The new songs associated with this input.
     */
    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    /**
     * Gets the date associated with this input.
     *
     * @return The date associated with this input.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date associated with this input.
     *
     * @param date The new date associated with this input.
     */
    public void setDate(final String date) {
        this.date = date;
    }

    /**
     * Gets the price associated with this input.
     *
     * @return The price associated with this input.
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Sets the price associated with this input.
     *
     * @param price The new price associated with this input.
     */
    public void setPrice(final Integer price) {
        this.price = price;
    }

    /**
     * Gets the episodes associated with this input.
     *
     * @return The episodes associated with this input.
     */
    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    /**
     * Sets the episodes associated with this input.
     *
     * @param episodes The new episodes associated with this input.
     */
    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }

    /**
     * Gets the next page associated with this input.
     *
     * @return The next page associated with this input.
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * Sets the next page associated with this input.
     *
     * @param nextPage The new next page associated with this input.
     */
    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * Gets the recommendation type associated with this input.
     *
     * @return The recommendation type associated with this input.
     */
    public String getRecommendationType() {
        return recommendationType;
    }

    /**
     * Sets the recommendation type associated with this input.
     *
     * @param recommendationType The new recommendation type associated with this input.
     */
    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }
}
