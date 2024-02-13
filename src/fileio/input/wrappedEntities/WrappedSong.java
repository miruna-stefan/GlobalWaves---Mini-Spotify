package fileio.input.wrappedEntities;

import fileio.input.audioEntities.SongPlayInfo;

public class WrappedSong extends GeneralEntityWrapped {
    private SongPlayInfo songPlayInfo;

    public WrappedSong(final SongPlayInfo songPlayInfo) {
        super();
        this.songPlayInfo = songPlayInfo;
    }

    /**
     * Gets the SongPlayInfo associated with this wrapped song.
     *
     * @return The SongPlayInfo associated with this wrapped song.
     */
    public SongPlayInfo getSongPlayInfo() {
        return songPlayInfo;
    }

    /**
     * Sets the SongPlayInfo associated with this wrapped song.
     *
     * @param songPlayInfo The new SongPlayInfo to be associated with this wrapped song.
     */
    public void setSongPlayInfo(final SongPlayInfo songPlayInfo) {
        this.songPlayInfo = songPlayInfo;
    }
}
