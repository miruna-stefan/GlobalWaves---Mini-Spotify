package fileio.input.audioEntities;

import users.NormalUser;

import java.util.ArrayList;

public class Album {
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<SongPlayInfo> songs;

    /* this list contains the same songs as the one above, but the order
    differs. This list is created after the shuffle status is activated */
    private ArrayList<SongPlayInfo> shuffledSongs;

    // (0) - no repeat, (1) - repeat all, (2) - repeat current song
    private Integer repeatStatus;

    // the position of the current song in the songs list
    private Integer currentSongIndex;

    // the second of the current song
    private Integer currentSongSecond;

    // (true) - paused, (false) - playing
    private Boolean paused;

    /* stores what was the last timestamp of an operation performed on the
    album while the album was on play */
    private Integer lastPlayTimestamp;

    // true - shuffle active ; false - shuffle inactive
    private Boolean shuffleStatus;

    // the total number of likes from all the album's songs
    private Integer numberOfLikes;

    /**
     * Constructs a new Album with the given name, release year, description, and list of songs.
     *
     * @param name          The name of the album.
     * @param releaseYear   The release year of the album.
     * @param description   The description of the album.
     * @param songs         The list of songs in the album.
     */
    public Album(final String name, final Integer releaseYear,
                 final String description, final ArrayList<SongPlayInfo> songs) {
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
        this.repeatStatus = 0;
        this.currentSongIndex = 0;
        this.paused = false;
        this.lastPlayTimestamp = 0;
        this.currentSongSecond = 0;
        this.shuffleStatus = false;
        this.numberOfLikes = 0;
    }

    /**
     * Updates the total number of likes for the album based on the list of songs.
     */
    public void updateAlbumNumberOfLikes() {
        Integer likes = 0;
        for (SongPlayInfo songPlayInfo : this.songs) {
            likes += songPlayInfo.getNumberOfLikes();
        }
        this.setNumberOfLikes(likes);
    }

    /**
     * Gets the names of the songs in the album.
     *
     * @return The list of song names in the album.
     */
    public ArrayList<String> getSongsNames() {
        ArrayList<String> songsNames = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : this.songs) {
            songsNames.add(songPlayInfo.getSong().getName());
        }
        return songsNames;
    }

    /**
     * Updates the status of the currently loaded album based on the user's actions.
     *
     * @param user              The user whose album status is to be updated.
     * @param currentTimestamp The current timestamp of the update.
     * @param albumSongs             The list of songs in the album.
     */
    public void updateAlbumStatus(final NormalUser user, final Integer currentTimestamp,
                                  final ArrayList<SongPlayInfo> albumSongs) {
        /* calculate the number of seconds that the song has been playing from
        the last update until the current timestamp */
        Integer playSeconds = currentTimestamp - this.getLastPlayTimestamp();
        Integer currentSecond = this.getCurrentSongSecond();
        Integer currentIndex = this.getCurrentSongIndex();

        int duration = albumSongs.get(currentIndex).getSong().getDuration();
        int carrySeconds = currentSecond + playSeconds - duration;

        // case no repeat
        if (user.getLastLoadedAlbum().getRepeatStatus() == 0) {
            // check if the song duration is exceeded
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            }
            while (carrySeconds > 0) {
                // check if this is the last song
                if (albumSongs.size() <= currentIndex + 1) {
                    // the album has ended
                    this.setCurrentSongSecond(0);
                    this.setCurrentSongIndex(0);
                    this.setPaused(true);
                    user.setLastLoadedAlbum(null);
                    user.setLoaded(false);
                    return;
                }

                /* if we reached this point, it means that the song
                is not the last one of the album */
                currentIndex++;
                user.updateEverythingForSong(albumSongs.get(currentIndex));
                if (user.getIsPremium()) {
                    user.updateSongAndArtistForPremiumUser(user.
                            getLastLoadedAlbum().getSongs().get(currentIndex));
                }

                /* check if the carry from the previous operation fits into the next
                  song or it extends even to the next next one and so on */
                if (carrySeconds > albumSongs.get(currentIndex).getSong().getDuration()) {
                    carrySeconds -= albumSongs.get(currentIndex).getSong().getDuration();
                } else {
                    if (carrySeconds == albumSongs.get(currentIndex).getSong().getDuration()) {
                        // the current song has finished and the next song is about be played
                        if (albumSongs.size() > currentIndex + 1) {
                            user.updateEverythingForSong(albumSongs.get(currentIndex + 1));
                        }
                    }
                    currentSecond = carrySeconds;
                    carrySeconds = 0;
                    this.setCurrentSongIndex(currentIndex);
                    this.setCurrentSongSecond(currentSecond);
                }
            }
        }

        // case repeat current song
        if (user.getLastLoadedAlbum().getRepeatStatus() == 2) {
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            } else {
                Integer totalSize = playSeconds + currentSecond;

                while (totalSize > albumSongs.get(currentIndex).getSong().getDuration()) {
                    totalSize -= albumSongs.get(currentIndex).getSong().getDuration();
                    user.updateEverythingForSong(albumSongs.get(currentIndex));
                    if (user.getIsPremium()) {
                        user.updateSongAndArtistForPremiumUser(user.
                                getLastLoadedAlbum().getSongs().get(currentIndex));
                    }
                }
                currentSecond = totalSize;
                user.getLastLoadedAlbum().setCurrentSongSecond(currentSecond);
            }
        }

        // case repeat once
        if (user.getLastLoadedAlbum().getRepeatStatus() == 1) {
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            } else {
                int totalSize = currentSecond + playSeconds;

                while (totalSize > albumSongs.get(currentIndex).getSong().getDuration()) {
                    if (albumSongs.size() <= currentIndex + 1) {
                        // this is the last song of the album
                        int remaining;
                        remaining = albumSongs.get(currentIndex).getSong().getDuration()
                                - currentSecond;
                        playSeconds = playSeconds - remaining;
                        currentIndex = 0;
                        user.updateEverythingForSong(albumSongs.get(currentIndex));
                        if (user.getIsPremium()) {
                            user.updateSongAndArtistForPremiumUser(user.
                                    getLastLoadedAlbum().getSongs().get(currentIndex));
                        }
                        currentSecond = 0;
                    } else {
                        int remaining;
                        remaining = albumSongs.get(currentIndex).getSong().getDuration()
                                - currentSecond;
                        playSeconds = playSeconds - remaining;
                        currentIndex++;
                        user.updateEverythingForSong(albumSongs.get(currentIndex));
                        if (user.getIsPremium()) {
                            user.updateSongAndArtistForPremiumUser(user.
                                    getLastLoadedAlbum().getSongs().get(currentIndex));
                        }
                        currentSecond = 0;
                    }
                    totalSize = currentSecond + playSeconds;
                }
                currentSecond = playSeconds;
                this.setCurrentSongSecond(currentSecond);
                this.setCurrentSongIndex(currentIndex);
            }

        }
    }

    /**
     * Gets the name of the album.
     *
     * @return The name of the album.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the album.
     *
     * @param name The new name for the album.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the release year of the album.
     *
     * @return The release year of the album.
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year of the album.
     *
     * @param releaseYear The new release year for the album.
     */
    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets the description of the album.
     *
     * @return The description of the album.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the album.
     *
     * @param description The new description for the album.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the list of songs in the album.
     *
     * @return The list of songs in the album.
     */
    public ArrayList<SongPlayInfo> getSongs() {
        return songs;
    }

    /**
     * Sets the list of songs in the album.
     *
     * @param songs The new list of songs for the album.
     */
    public void setSongs(final ArrayList<SongPlayInfo> songs) {
        this.songs = songs;
    }

    /**
     * Gets the list of shuffled songs in the album.
     *
     * @return The list of shuffled songs in the album.
     */
    public ArrayList<SongPlayInfo> getShuffledSongs() {
        return shuffledSongs;
    }

    /**
     * Sets the list of shuffled songs in the album.
     *
     * @param shuffledSongs The new list of shuffled songs for the album.
     */
    public void setShuffledSongs(final ArrayList<SongPlayInfo> shuffledSongs) {
        this.shuffledSongs = shuffledSongs;
    }

    /**
     * Gets the repeat status of the album.
     *
     * @return The repeat status of the album (0 for no repeat, 1 for repeat all,
     * 2 for repeat current song).
     */
    public Integer getRepeatStatus() {
        return repeatStatus;
    }

    /**
     * Sets the repeat status of the album.
     *
     * @param repeatStatus The new repeat status for the album.
     */
    public void setRepeatStatus(final Integer repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    /**
     * Gets the index of the current song in the album.
     *
     * @return The index of the current song in the album.
     */
    public Integer getCurrentSongIndex() {
        return currentSongIndex;
    }

    /**
     * Sets the index of the current song in the album.
     *
     * @param currentSongIndex The new index of the current song in the album.
     */
    public void setCurrentSongIndex(final Integer currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    /**
     * Gets the current second of the current song in the album.
     *
     * @return The current second of the current song in the album.
     */
    public Integer getCurrentSongSecond() {
        return currentSongSecond;
    }

    /**
     * Sets the current second of the current song in the album.
     *
     * @param currentSongSecond The new current second of the current song in the album.
     */
    public void setCurrentSongSecond(final Integer currentSongSecond) {
        this.currentSongSecond = currentSongSecond;
    }

    /**
     * Gets the paused status of the album.
     *
     * @return True if the album is paused, false if it's playing.
     */
    public Boolean getPaused() {
        return paused;
    }

    /**
     * Sets the paused status of the album.
     *
     * @param paused The new paused status for the album.
     */
    public void setPaused(final Boolean paused) {
        this.paused = paused;
    }

    /**
     * Gets the last play timestamp of the album.
     *
     * @return The last play timestamp of the album.
     */
    public Integer getLastPlayTimestamp() {
        return lastPlayTimestamp;
    }

    /**
     * Sets the last play timestamp of the album.
     *
     * @param lastPlayTimestamp The new last play timestamp for the album.
     */
    public void setLastPlayTimestamp(final Integer lastPlayTimestamp) {
        this.lastPlayTimestamp = lastPlayTimestamp;
    }

    /**
     * Gets the shuffle status of the album.
     *
     * @return True if shuffle is active, false if it's inactive.
     */
    public Boolean getShuffleStatus() {
        return shuffleStatus;
    }

    /**
     * Sets the shuffle status of the album.
     *
     * @param shuffleStatus True if shuffle is active, false if it's inactive.
     */
    public void setShuffleStatus(final Boolean shuffleStatus) {
        this.shuffleStatus = shuffleStatus;
    }

    /**
     * Gets the total number of likes from all the album's songs.
     *
     * @return The total number of likes from all the album's songs.
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * Sets the total number of likes from all the album's songs.
     *
     * @param numberOfLikes The new total number of likes for the album.
     */
    public void setNumberOfLikes(final Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
}
