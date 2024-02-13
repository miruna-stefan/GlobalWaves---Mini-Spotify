package fileio.input.wrappedEntities;

public class WrappedArtist extends GeneralEntityWrapped {
    private String artistName;

    public WrappedArtist(final String artistName) {
        super();
        this.artistName = artistName;
    }

    /**
     * Gets the name of the artist associated with this wrapped artist.
     *
     * @return The name of the artist associated with this wrapped artist.
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * Sets the name of the artist associated with this wrapped artist.
     *
     * @param artistName The new artist name to be associated with this wrapped artist.
     */
    public void setArtistName(final String artistName) {
        this.artistName = artistName;
    }
}
