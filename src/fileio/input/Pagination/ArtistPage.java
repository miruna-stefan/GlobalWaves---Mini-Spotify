package fileio.input.Pagination;

import fileio.input.audioEntities.Album;
import fileio.input.entitiesForArtist.Event;
import fileio.input.entitiesForArtist.Merch;
import users.Artist;

import java.util.ArrayList;

public class ArtistPage extends Page {
    private static final int TYPE_ARTIST = 5;
    private Artist artistPageOwner;

    public ArtistPage(final Artist artistPageOwner) {
        super(TYPE_ARTIST);
        this.artistPageOwner = artistPageOwner;
    }

    /**
     * Converts the ArtistPage object to its string representation.
     *
     * @return A formatted string representation of the ArtistPage.
     */
    @Override
    public String pageToString() {
        return "Albums:\n\t" + getAlbumNames(this.artistPageOwner) + "\n\nMerch:\n\t"
                + this.getMerchStringList(this.artistPageOwner) + "\n\nEvents:\n\t"
                + this.getEventStringList(this.artistPageOwner);
    }

    /**
     * Retrieves and formats the names of albums for an Artist.
     *
     * @param artist the Artist for whom album names are retrieved.
     * @return an ArrayList of strings containing the names of albums.
     */
    public ArrayList<String> getAlbumNames(final Artist artist) {
        ArrayList<String> albumNames = new ArrayList<>();
        for (Album album : artist.getAlbums()) {
            albumNames.add(album.getName());
        }
        return albumNames;
    }

    /**
     * Retrieves and formats the details of events for an Artist.
     *
     * @param artist the Artist for whom event details are retrieved.
     * @return an ArrayList of strings containing details about events.
     */
    public ArrayList<String> getEventStringList(final Artist artist) {
        ArrayList<String> eventStringList = new ArrayList<>();
        for (Event event : artist.getEvents()) {
            String eventString = event.getName() + " - " + event.getDate()
                    + ":\n\t" + event.getDescription();
            eventStringList.add(eventString);
        }
        return eventStringList;
    }

    /**
     * Retrieves and formats the details of merch items for an Artist.
     *
     * @param artist the Artist for whom merch details are retrieved.
     * @return an ArrayList of strings containing details about merch items.
     */
    public ArrayList<String> getMerchStringList(final Artist artist) {
        ArrayList<String> merchStringList = new ArrayList<>();
        for (Merch merch : artist.getMerchItems()) {
            String merchString = merch.getName() + " - " + merch.getPrice()
                    + ":\n\t" + merch.getDescription();
            merchStringList.add(merchString);
        }
        return merchStringList;
    }

    /**
     * Gets the Artist who owns this page.
     *
     * @return The Artist who owns this page.
     */
    public Artist getArtistPageOwner() {
        return artistPageOwner;
    }

    /**
     * Sets the Artist who owns this page.
     *
     * @param artistPageOwner The new Artist who owns this page.
     */
    public void setArtistPageOwner(final Artist artistPageOwner) {
        this.artistPageOwner = artistPageOwner;
    }
}
