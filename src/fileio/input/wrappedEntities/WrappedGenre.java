package fileio.input.wrappedEntities;

public class WrappedGenre extends GeneralEntityWrapped {
    private String genre;

    public WrappedGenre(final String genre) {
        super();
        this.genre = genre;
    }

    /**
     * Gets the genre information.
     *
     * @return The genre information.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre information.
     *
     * @param genre The new genre information to be set.
     */
    public void setGenre(final String genre) {
        this.genre = genre;
    }
}
