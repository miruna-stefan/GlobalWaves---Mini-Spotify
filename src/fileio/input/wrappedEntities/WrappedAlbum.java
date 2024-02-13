package fileio.input.wrappedEntities;

public class WrappedAlbum extends GeneralEntityWrapped {
    private String albumName;

    public WrappedAlbum(final String albumName) {
        super();
        this.albumName = albumName;
    }

    /**
     * Gets the name of the album associated with this wrapped album.
     *
     * @return The name of the album associated with this wrapped album.
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Sets the name of the album associated with this wrapped album.
     *
     * @param albumName The new album name to be associated with this wrapped album.
     */
    public void setAlbumName(final String albumName) {
        this.albumName = albumName;
    }
}
