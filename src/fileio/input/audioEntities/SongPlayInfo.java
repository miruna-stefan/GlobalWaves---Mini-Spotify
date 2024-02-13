package fileio.input.audioEntities;

import users.NormalUser;

public class SongPlayInfo {
    private SongInput song;

    // the last timestamp when the command pause was received for this song
    private Integer lastPlayTimestamp;

    // (0) - no repeat, (1) - repeat once, (2) - repeat infinite
    private Integer repeatStatus;

    // true - song is paused, false - song is playing
    private Boolean songPaused;

    private Integer currentSecond;

    private Integer numberOfLikes;

    public SongPlayInfo() {
        this.numberOfLikes = 0;
    }

    /**
     * Updates the status of the currently loaded song based on the user's actions.
     *
     * @param user              The user whose song status is to be updated.
     * @param currentTimestamp The current timestamp of the update.
     */
    public void updateSongStatus(final NormalUser user, final Integer currentTimestamp) {

        /* calculate the number of seconds that the song has been playing form
        the last update until the current timestamp */
        Integer playSeconds = currentTimestamp - this.getLastPlayTimestamp();
        Integer currentSec = this.getCurrentSecond();

        // case no repeat
        if (this.getRepeatStatus() == 0) {
            if (this.getCurrentSecond() + playSeconds > this.getSong().getDuration()) {
                // the song has finished. Nothing is playing anymore
                this.setCurrentSecond(0);
                user.setLastLoadedSongPlayInfo(null);
                user.setLoaded(false);
                return;
            }

            // if we are here, it means that the song hasn't finished yet
            this.setCurrentSecond(currentSec + playSeconds);
            return;
        }

        // case repeat once
        if (this.getRepeatStatus() == 1) {
            if (this.getCurrentSecond() + playSeconds > 2 * this.getSong().getDuration()) {
                // the song has been played 2 times and has ended
                user.setLastLoadedSongPlayInfo(null);
                user.setLoaded(false);
                this.setRepeatStatus(0);
                user.updateEverythingForSong(this);
                if (user.getIsPremium()) {
                    user.updateSongAndArtistForPremiumUser(this);
                }
                return;
            }
            // check if the song duration is exceeded
            if (this.getCurrentSecond() + playSeconds > this.getSong().getDuration()) {
                user.updateEverythingForSong(this);
                if (user.getIsPremium()) {
                    user.updateSongAndArtistForPremiumUser(this);
                }
                playSeconds += this.getCurrentSecond() - this.getSong().getDuration();
                currentSec = playSeconds;
                this.setCurrentSecond(currentSec);
                this.setRepeatStatus(0);
                return;
            }
            this.setCurrentSecond(currentSec + playSeconds);
            return;
        }

        // case repeat infinite
        if (this.getRepeatStatus() == 2) {
            // check if the song duration is exceeded
            if (this.getCurrentSecond() + playSeconds <= this.getSong().getDuration()) {
                this.setCurrentSecond(currentSec + playSeconds);
            } else {
                playSeconds = playSeconds - (this.getSong().getDuration() - currentSec);
                user.updateEverythingForSong(this);
                if (user.getIsPremium()) {
                    user.updateSongAndArtistForPremiumUser(this);
                }
                while (playSeconds > this.getSong().getDuration()) {
                    playSeconds = playSeconds - this.getSong().getDuration();
                    user.updateEverythingForSong(this);
                    if (user.getIsPremium()) {
                        user.updateSongAndArtistForPremiumUser(this);
                    }
                }
                currentSec = playSeconds;
                this.setCurrentSecond(currentSec);
            }
        }
    }

    /**
     * Gets the song associated with this play information.
     *
     * @return The SongInput object representing the song.
     */
    public SongInput getSong() {
        return song;
    }

    /**
     * Sets the song for this play information.
     *
     * @param song The SongInput object representing the song to set.
     */
    public void setSong(final SongInput song) {
        this.song = song;
    }

    /**
     * Gets the last play timestamp for the song.
     *
     * @return The last play timestamp for the song.
     */
    public Integer getLastPlayTimestamp() {
        return lastPlayTimestamp;
    }

    /**
     * Sets the last play timestamp for the song.
     *
     * @param lastPlayTimestamp The new last play timestamp for the song.
     */
    public void setLastPlayTimestamp(final Integer lastPlayTimestamp) {
        this.lastPlayTimestamp = lastPlayTimestamp;
    }

    /**
     * Gets the repeat status for the song.
     *
     * @return The repeat status for the song (0 for no repeat,
     * 1 for repeat once, 2 for repeat infinite).
     */
    public Integer getRepeatStatus() {
        return repeatStatus;
    }

    /**
     * Sets the repeat status for the song.
     *
     * @param repeatStatus The new repeat status for the song.
     */
    public void setRepeatStatus(final Integer repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    /**
     * Gets the paused status for the song.
     *
     * @return True if the song is paused, false if it's playing.
     */
    public Boolean getSongPaused() {
        return songPaused;
    }

    /**
     * Sets the paused status for the song.
     *
     * @param songPaused True if the song is paused, false if it's playing.
     */
    public void setSongPaused(final Boolean songPaused) {
        this.songPaused = songPaused;
    }

    /**
     * Gets the current second of the song's playback.
     *
     * @return The current second of the song's playback.
     */
    public Integer getCurrentSecond() {
        return currentSecond;
    }

    /**
     * Sets the current second of the song's playback.
     *
     * @param currentSecond The new current second for the song's playback.
     */
    public void setCurrentSecond(final Integer currentSecond) {
        this.currentSecond = currentSecond;
    }

    /**
     * Gets the number of likes for the song.
     *
     * @return The number of likes for the song.
     */
    public Integer getNumberOfLikes() {
        return numberOfLikes;
    }

    /**
     * Sets the number of likes for the song.
     *
     * @param numberOfLikes The new number of likes for the song.
     */
    public void setNumberOfLikes(final Integer numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }
}
