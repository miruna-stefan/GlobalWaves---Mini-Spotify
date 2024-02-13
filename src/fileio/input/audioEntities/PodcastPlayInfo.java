package fileio.input.audioEntities;

import users.NormalUser;

import java.util.ArrayList;

public class PodcastPlayInfo {
    private PodcastInput podcast;

    // the index of the current episode of the podcast
    private Integer currentEpisodeIndex;

    // the current second of the current podcast episode
    private Integer currentSecondEpisode;

    // (0) - no repeat, (1) - repeat once, (2) - repeat infinite
    private Integer repeatStatus;

    /* variable that stores the timestamp of the last command
    applied to the podcast (except from pause) */
    private Integer lastPlayTimestamp;

    // indicates if a podcast is paused or playing
    private Boolean podcastPaused;

    public PodcastPlayInfo(final PodcastInput podcast,
                           final Integer currentEpisodeIndex, final Integer currentSecondEpisode) {
        this.podcast = podcast;
        this.currentEpisodeIndex = currentEpisodeIndex;
        this.currentSecondEpisode = currentSecondEpisode;
        this.repeatStatus = 0;
        this.lastPlayTimestamp = 0;
        this.podcastPaused = true;
    }

    /**
     * Updates the status of the currently loaded podcast based on the user's actions.
     *
     * @param user              The user whose podcast status is to be updated.
     * @param currentTimestamp The current timestamp of the update.
     */
    public void updatePodcastStatus(final NormalUser user, final Integer currentTimestamp) {
        /* calculate the number of seconds that the song has been playing from
        the last update until the current timestamp */
        Integer playSeconds = currentTimestamp - this.getLastPlayTimestamp();

        // find out the duration of the current episode
        Integer currentIndex;
        Integer currentEpisodeSecond;
        currentEpisodeSecond = this.getCurrentSecondEpisode();
        currentIndex = this.getCurrentEpisodeIndex();

        EpisodeInput currentEpisode;
        currentEpisode = this.getPodcast().getEpisodes().get(currentIndex);
        int duration = currentEpisode.getDuration();
        int carrySeconds = currentEpisodeSecond + playSeconds - duration;

        // case no repeat
        if (this.getRepeatStatus() == 0) {
            if (carrySeconds <= 0) {
                // we are situated within the bounds of the same episode
                this.setCurrentSecondEpisode(currentEpisodeSecond + playSeconds);
            }
            while (carrySeconds > 0) {
                // check if this is the last episode
                if (this.getPodcast().getEpisodes().size() <= currentIndex + 1) {
                    // the podcast has ended

                    // case no repeat
                    this.setCurrentEpisodeIndex(0);
                    this.setCurrentSecondEpisode(0);
                    this.setPodcastPaused(true);
                    user.setLastLoadedPodcast(null);
                    user.setLoaded(false);
                    return;
                }

                /* if we reached this point, it means that the episode is not the
                last one of the podcast */
                currentIndex++;
                user.updateWrappedEpisodes(this.getPodcast().getEpisodes().get(currentIndex));

                /* check if the carry from the previous operation fits into the
                next episode or it extends even to the next next one and so on */

                if (carrySeconds > duration) {
                    carrySeconds = carrySeconds - duration;
                } else {
                    currentEpisodeSecond = carrySeconds;
                    carrySeconds = 0;
                    this.setCurrentEpisodeIndex(currentIndex);
                    this.setCurrentSecondEpisode(currentEpisodeSecond);
                }
            }
        }

        // case repeat current episode inifinite
        if (this.getRepeatStatus() == 2) {
            ArrayList<EpisodeInput> episodes;
            episodes = this.getPodcast().getEpisodes();
            Integer currentEpisodeDuration = episodes.get(currentIndex).getDuration();
            // check if we are still within the bounds of the same episode
            if (this.getCurrentSecondEpisode() + playSeconds <= currentEpisodeDuration) {
                // we are still within the bounds of the same episode
                this.setCurrentSecondEpisode(currentEpisodeSecond + playSeconds);
            } else {
                /* we have exceeded the duration of the current episode,
                so we should replay it from the beginning */
                playSeconds = playSeconds - (currentEpisodeDuration - currentEpisodeSecond);
                user.updateWrappedEpisodes(this.getPodcast().getEpisodes().get(currentIndex));
                while (playSeconds > currentEpisodeDuration) {
                    playSeconds = playSeconds - currentEpisodeDuration;
                    user.updateWrappedEpisodes(this.getPodcast().getEpisodes().get(currentIndex));
                }
                currentEpisodeSecond = playSeconds;
                this.setCurrentSecondEpisode(currentEpisodeSecond);
            }
        }

        // case repeat episode once
        if (this.getRepeatStatus() == 1) {
            ArrayList<EpisodeInput> episodes;
            episodes = this.getPodcast().getEpisodes();
            Integer currentEpisodeDuration = episodes.get(currentIndex).getDuration();

            // check if the episode duration is exceeded or not
            if (this.getCurrentSecondEpisode() + playSeconds <= currentEpisodeDuration) {
                // we are still within the bounds of the same episode
                this.setCurrentSecondEpisode(currentEpisodeSecond + playSeconds);
            } else {
                playSeconds = playSeconds + currentEpisodeSecond - currentEpisodeDuration;
                user.updateWrappedEpisodes(this.getPodcast().getEpisodes().get(currentIndex));
                currentEpisodeSecond = playSeconds;
                this.setCurrentSecondEpisode(currentEpisodeSecond);
                this.setRepeatStatus(0);
            }

        }
        this.setPodcastPaused(true);

    }

    /**
     * Gets the index of the current episode being played.
     *
     * @return The index of the current episode being played.
     */
    public Integer getCurrentEpisodeIndex() {
        return currentEpisodeIndex;
    }

    /**
     * Sets the index of the current episode being played.
     *
     * @param currentEpisodeIndex The new index of the current episode.
     */
    public void setCurrentEpisodeIndex(final Integer currentEpisodeIndex) {
        this.currentEpisodeIndex = currentEpisodeIndex;
    }

    /**
     * Gets the current play time in seconds for the current episode.
     *
     * @return The current play time in seconds for the current episode.
     */
    public Integer getCurrentSecondEpisode() {
        return currentSecondEpisode;
    }

    /**
     * Sets the current play time in seconds for the current episode.
     *
     * @param currentSecondEpisode The new current play time in seconds.
     */
    public void setCurrentSecondEpisode(final Integer currentSecondEpisode) {
        this.currentSecondEpisode = currentSecondEpisode;
    }

    /**
     * Gets the associated podcast.
     *
     * @return The associated podcast.
     */
    public PodcastInput getPodcast() {
        return podcast;
    }

    /**
     * Sets the associated podcast.
     *
     * @param podcast The new associated podcast.
     */
    public void setPodcast(final PodcastInput podcast) {
        this.podcast = podcast;
    }

    /**
     * Gets the repeat status for the podcast.
     *
     * @return The repeat status for the podcast.
     * (0) - no repeat, (1) - repeat once, (2) - repeat infinite.
     */
    public Integer getRepeatStatus() {
        return repeatStatus;
    }

    /**
     * Sets the repeat status for the podcast.
     *
     * @param repeatStatus The new repeat status for the podcast.
     */
    public void setRepeatStatus(final Integer repeatStatus) {
        this.repeatStatus = repeatStatus;
    }

    /**
     * Gets the timestamp of the last command applied to the podcast (except for pause).
     *
     * @return The timestamp of the last command applied to the podcast.
     */
    public Integer getLastPlayTimestamp() {
        return lastPlayTimestamp;
    }

    /**
     * Sets the timestamp of the last command applied to the podcast (except for pause).
     *
     * @param lastPlayTimestamp The new timestamp of the last command applied to the podcast.
     */
    public void setLastPlayTimestamp(final Integer lastPlayTimestamp) {
        this.lastPlayTimestamp = lastPlayTimestamp;
    }

    /**
     * Gets the pause/play status of the podcast.
     *
     * @return True if the podcast is paused, false if it is playing.
     */
    public Boolean getPodcastPaused() {
        return podcastPaused;
    }

    /**
     * Sets the pause/play status of the podcast.
     *
     * @param podcastPaused True if the podcast is paused, false if it is playing.
     */
    public void setPodcastPaused(final Boolean podcastPaused) {
        this.podcastPaused = podcastPaused;
    }
}
