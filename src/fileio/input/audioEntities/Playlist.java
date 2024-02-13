package fileio.input.audioEntities;

import main.Main;
import users.Artist;
import users.NormalUser;

import java.util.ArrayList;

/**
 * Represents a playlist with songs and associated metadata.
 */
public class Playlist {
    // list of the songs of the playlist when shuffle mode is NOT active
    private ArrayList<SongPlayInfo> playlistSongs;

    /* this list contains the same songs as the one above, but the order
    differs. This list is created after the shuffle status is activated */
    private ArrayList<SongPlayInfo> shuffledPlaylistSongs;

    // (true) - private, (false) - public
    private Boolean visibility;

    // the name of the playlist
    private String name;

    // the username of the user that has created the playlist
    private String owner;

    // (0) - no repeat, (1) - repeat all, (2) - repeat current song
    private Integer repeatStatus;

    // the position of the current song in the songs list
    private Integer currentSongIndex;

    // the second of the current song
    private Integer currentSongSecond;

    // (true) - paused, (false) - playing
    private Boolean paused;

    /* stores what was the last timestamp of an operation performed on the
    playlist while the playlist was on play */
    private Integer lastPlayTimestamp;

    // true - shuffle active ; false - shuffle inactive
    private Boolean shuffleStatus;

    // store the number of followers of the playlist
    private Integer followers;

    // store the total number of likes from all the songs of the playlist
    private Integer numberOfLikes;

    /**
     * Constructs a new Playlist with the given name, visibility, and owner.
     *
     * @param name       The name of the playlist.
     * @param visibility The visibility status of the playlist (true for private, false for public).
     * @param owner      The username of the user who created the playlist.
     */
    public Playlist(final String name, final Boolean visibility, final String owner) {
        this.name = name;
        this.visibility = visibility;
        this.owner = owner;
        playlistSongs = new ArrayList<>();
        this.repeatStatus = 0;
        this.currentSongIndex = 0;
        this.paused = false;
        this.lastPlayTimestamp = 0;
        this.currentSongSecond = 0;
        this.shuffleStatus = false;
        this.followers = 0;
        this.numberOfLikes = 0;
    }


    /**
     * Updates the total number of likes for the playlist based on the given list of songs.
     *
     * @param songsInPlaylist The list of songs in the playlist.
     */
    public void updateNumberOfLikes(final ArrayList<SongPlayInfo> songsInPlaylist) {
        Integer totalLikes = 0;
        for (SongPlayInfo songPlayInfo : songsInPlaylist) {
            totalLikes += songPlayInfo.getNumberOfLikes();
        }
        this.numberOfLikes = totalLikes;
    }

    /**
     * Updates the status of the currently loaded playlist based on the user's actions.
     *
     * @param user             The user whose playlist status is to be updated.
     * @param currentTimestamp The current timestamp of the update.
     * @param songs            The list of songs in the playlist.
     */
    public void updatePlaylistStatus(final NormalUser user, final Integer currentTimestamp,
                                     final ArrayList<SongPlayInfo> songs) {
        /* calculate the number of seconds that the song has been playing from
        the last update until the current timestamp */
        Integer playSeconds = currentTimestamp - this.getLastPlayTimestamp();
        Integer currentSecond = this.getCurrentSongSecond();
        Integer currentIndex = this.getCurrentSongIndex();

        if (songs.isEmpty()) {
            return;
        }
        int duration = songs.get(currentIndex).getSong().getDuration();
        int carrySeconds = currentSecond + playSeconds - duration;

        // case no repeat
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 0) {
            // check if the song duration is exceeded
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            }
            while (carrySeconds > 0) {
                // check if this is the last song
                if (songs.size() <= currentIndex + 1) {
                    // the playlist has ended
                    this.setCurrentSongSecond(0);
                    this.setCurrentSongIndex(0);
                    this.setPaused(true);
                    user.setLastLoadedPlaylist(null);
                    return;

                }

                /* if we reached this point, it means that the song
                is not the last one of the playlist */
                currentIndex++;
                user.updateEverythingForSong(songs.get(currentIndex));
                if (user.getIsPremium()) {
                    user.updateSongAndArtistForPremiumUser(user.getLastLoadedPlaylist().
                            getPlaylistSongs().get(currentIndex));
                }

                for (Artist artist : Main.artistsList) {
                    if (artist.getUsername().equals(songs.get(currentIndex).
                            getSong().getArtist())) {
                        artist.setHadSomethingOnPlay(true);
                        break;
                    }
                }

                /* check if the carry from the previous operation fits into the next
                  song or it extends even to the next next one and so on */
                if (carrySeconds > songs.get(currentIndex).getSong().getDuration()) {
                    carrySeconds -= songs.get(currentIndex).getSong().getDuration();
                } else {
                    if (carrySeconds == songs.get(currentIndex).getSong().getDuration()) {
                        // the current song has finished and the next song is about be played
                        if (songs.size() > currentIndex + 1) {
                            user.updateEverythingForSong(songs.get(currentIndex + 1));
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
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 2) {
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            } else {
                Integer totalSize = playSeconds + currentSecond;

                while (totalSize > songs.get(currentIndex).getSong().getDuration()) {
                    totalSize -= songs.get(currentIndex).getSong().getDuration();
                    user.updateEverythingForSong(songs.get(currentIndex));
                    if (user.getIsPremium()) {
                        user.updateSongAndArtistForPremiumUser(user.
                                getLastLoadedPlaylist().getPlaylistSongs().get(currentIndex));
                    }
                }
                currentSecond = totalSize;
                user.getLastLoadedPlaylist().setCurrentSongSecond(currentSecond);
            }
        }

        // case repeat once
        if (user.getLastLoadedPlaylist().getRepeatStatus() == 1) {
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same song
                this.setCurrentSongSecond(currentSecond + playSeconds);
            } else {
                int totalSize = currentSecond + playSeconds;

                while (totalSize > songs.get(currentIndex).getSong().getDuration()) {
                    if (songs.size() <= currentIndex + 1) {
                        // this is the last song of the playlist
                        int remaining;
                        remaining = songs.get(currentIndex).getSong().getDuration() - currentSecond;
                        playSeconds = playSeconds - remaining;
                        currentIndex = 0;
                        for (Artist artist : Main.artistsList) {
                            if (artist.getUsername().equals(songs.get(currentIndex).
                                    getSong().getArtist())) {
                                artist.setHadSomethingOnPlay(true);
                                break;
                            }
                        }
                        user.updateEverythingForSong(songs.get(currentIndex));
                        if (user.getIsPremium()) {
                            user.updateSongAndArtistForPremiumUser(user.
                                    getLastLoadedPlaylist().getPlaylistSongs().get(currentIndex));
                        }
                        currentSecond = 0;
                    } else {
                        int remaining;
                        remaining = songs.get(currentIndex).getSong().getDuration() - currentSecond;
                        playSeconds = playSeconds - remaining;
                        currentIndex++;
                        for (Artist artist : Main.artistsList) {
                            if (artist.getUsername().equals(songs.get(currentIndex).
                                    getSong().getArtist())) {
                                artist.setHadSomethingOnPlay(true);
                                break;
                            }
                        }
                        user.updateEverythingForSong(songs.get(currentIndex));
                        if (user.getIsPremium()) {
                            user.updateSongAndArtistForPremiumUser(user.getLastLoadedPlaylist().
                                    getPlaylistSongs().get(currentIndex));
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
     * Gets the username of the owner of the playlist.
     *
     * @return The username of the owner.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the playlist.
     *
     * @param owner The username of the new owner.
     */
    public void setOwner(final String owner) {
        this.owner = owner;
    }

    /**
     * Gets the name of the playlist.
     *
     * @return The name of the playlist.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the playlist.
     *
     * @param name The new name for the playlist.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the list of songs in the playlist.
     *
     * @return The list of songs in the playlist.
     */
    public ArrayList<SongPlayInfo> getPlaylistSongs() {
        return playlistSongs;
    }

    /**
     * Sets the list of songs in the playlist.
     *
     * @param playlistSongs The new list of songs for the playlist.
     */
    public void setPlaylistSongs(final ArrayList<SongPlayInfo> playlistSongs) {
        this.playlistSongs = playlistSongs;
    }

    /**
     * Gets the visibility status of the playlist.
     *
     * @return True if the playlist is private, false if it's public.
     */
    public Boolean getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility status of the playlist.
     *
     * @param visibility The new visibility status for the playlist.
     */
    public void setVisibility(final Boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Gets the repeat status of the playlist.
     *
     * @return The repeat status of the playlist (0 for no repeat,
     * 1 for repeat all, 2 for repeat current song).
     */
    public Integer getRepeatStatus() {
        return repeatStatus;
    }

    /**
     * Sets the repeat status of the playlist.
     *
     * @param repeatStatus The new repeat status for the playlist.
     */
    public void setRepeatStatus(final Integer repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    /**
     * Gets the list of song names in the playlist.
     *
     * @return The list of song names in the playlist.
     */
    public ArrayList<String> getSongsNames() {
        ArrayList<String> songNames = new ArrayList<>();
        for (SongPlayInfo songPlayInfo : this.getPlaylistSongs()) {
            String songName = songPlayInfo.getSong().getName();
            songNames.add(songName);
        }
        return songNames;
    }

    /**
     * Gets the index of the current song in the playlist.
     *
     * @return The index of the current song.
     */
    public Integer getCurrentSongIndex() {
        return this.currentSongIndex;
    }

    /**
     * Sets the index of the current song in the playlist.
     *
     * @param currentSongIndex The new index of the current song.
     */
    public void setCurrentSongIndex(final Integer currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    /**
     * Gets the paused status of the playlist.
     *
     * @return True if the playlist is paused, false if it's playing.
     */
    public Boolean getPaused() {
        return paused;
    }

    /**
     * Sets the paused status of the playlist.
     *
     * @param paused The new paused status for the playlist.
     */
    public void setPaused(final Boolean paused) {
        this.paused = paused;
    }

    /**
     * Gets the last play timestamp of the playlist.
     *
     * @return The last play timestamp of the playlist.
     */
    public Integer getLastPlayTimestamp() {
        return lastPlayTimestamp;
    }

    /**
     * Sets the last play timestamp of the playlist.
     *
     * @param lastPlayTimestamp The new last play timestamp for the playlist.
     */
    public void setLastPlayTimestamp(final Integer lastPlayTimestamp) {
        this.lastPlayTimestamp = lastPlayTimestamp;
    }

    /**
     * Gets the current second of the current song in the playlist.
     *
     * @return The current second of the current song.
     */
    public Integer getCurrentSongSecond() {
        return currentSongSecond;
    }

    /**
     * Sets the current second of the current song in the playlist.
     *
     * @param currentSongSecond The new current second of the current song.
     */
    public void setCurrentSongSecond(final Integer currentSongSecond) {
        this.currentSongSecond = currentSongSecond;
    }

    /**
     * Gets the list of shuffled songs in the playlist.
     *
     * @return The list of shuffled songs in the playlist.
     */
    public ArrayList<SongPlayInfo> getShuffledPlaylistSongs() {
        return shuffledPlaylistSongs;
    }

    /**
     * Sets the list of shuffled songs in the playlist.
     *
     * @param shuffledPlaylistSongs The new list of shuffled songs for the playlist.
     */
    public void setShuffledPlaylistSongs(final ArrayList<SongPlayInfo> shuffledPlaylistSongs) {
        this.shuffledPlaylistSongs = shuffledPlaylistSongs;
    }

    /**
     * Gets the shuffle status of the playlist.
     *
     * @return True if shuffle is active, false if it's inactive.
     */
    public Boolean getShuffleStatus() {
        return shuffleStatus;
    }

    /**
     * Sets the shuffle status of the playlist.
     *
     * @param shuffleStatus The new shuffle status for the playlist.
     */
    public void setShuffleStatus(final Boolean shuffleStatus) {
        this.shuffleStatus = shuffleStatus;
    }

    /**
     * Gets the number of followers of the playlist.
     *
     * @return The number of followers of the playlist.
     */
    public Integer getFollowers() {
        return followers;
    }

    /**
     * Sets the number of followers of the playlist.
     *
     * @param followers The new number of followers for the playlist.
     */
    public void setFollowers(final Integer followers) {
        this.followers = followers;
    }

    /**
     * Gets the number of likes for the playlist.
     *
     * @return The number of likes for the playlist.
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * Sets the number of likes for the playlist.
     *
     * @param numberOfLikes The new number of likes for the playlist.
     */
    public void setNumberOfLikes(final Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
}
