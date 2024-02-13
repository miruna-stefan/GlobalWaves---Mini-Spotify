package fileio.input.wrappedEntities;

import fileio.input.audioEntities.EpisodeInput;

public class WrappedEpisode extends GeneralEntityWrapped {
    private EpisodeInput episode;

    public WrappedEpisode(final EpisodeInput episode) {
        super();
        this.episode = episode;
    }

    /**
     * Gets the EpisodeInput associated with this wrapped episode.
     *
     * @return The EpisodeInput associated with this wrapped episode.
     */
    public EpisodeInput getEpisode() {
        return episode;
    }

    /**
     * Sets the EpisodeInput associated with this wrapped episode.
     *
     * @param episode The new EpisodeInput to be associated with this wrapped episode.
     */
    public void setEpisode(final EpisodeInput episode) {
        this.episode = episode;
    }
}
