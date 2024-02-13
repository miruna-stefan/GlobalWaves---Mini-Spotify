package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandsHandling.GeneralCommand;
import users.Artist;
import users.NormalUser;
import fileio.input.wrappedEntities.MonetizedSong;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class EndProgram extends GeneralCommand {
    private static final double HUNDRED = 100.0;

    private static EndProgram instance = null;

    private EndProgram(final String command, final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node);
        this.timestamp = null;
    }

    /**
     * Gets the singleton instance of the EndProgram class.
     *
     * @param command   The command string.
     * @param timestamp The timestamp associated with the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of the EndProgram class.
     */
    public static EndProgram getInstance(final String command, final Integer timestamp,
                                         final ObjectNode node) {
        if (instance == null) {
            instance = new EndProgram(command, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Reorders eligible artists based on merch and song revenue in descending order.
     *
     * @param eligibleArtists The list of eligible artists to be reordered.
     */
    public void reorderEligibleArtists(final ArrayList<Artist> eligibleArtists) {
        eligibleArtists.sort(new Comparator<Artist>() {
            @Override
            public int compare(final Artist artist1, final Artist artist2) {
                // if the merchRevenue is equal, sort in lexicographical order
                if (Objects.equals(artist1.getMerchRevenue(), artist2.getMerchRevenue())) {
                    if (Objects.equals(artist1.getSongRevenue(), artist2.getSongRevenue())) {
                        return artist1.getUsername().compareTo(artist2.getUsername());
                    }
                    return (int) (artist2.getSongRevenue() - artist1.getSongRevenue());
                }
                return (int) (artist2.getMerchRevenue() - artist1.getMerchRevenue());
            }
        });
    }

    /**
     * Gets the name of the most profitable song for an artist.
     *
     * @param artist The artist for whom the most profitable song is retrieved.
     * @return The name of the most profitable song.
     */
    public String getMostProfitableSongName(final Artist artist) {
        // sort the list in a lexicographical order
        artist.getMonetizedSongs().sort(new Comparator<MonetizedSong>() {
            @Override
            public int compare(final MonetizedSong song1, final MonetizedSong song2) {
                return song1.getSongPlayInfo().getSong().getName().compareTo(song2.
                        getSongPlayInfo().getSong().getName());
            }
        });

        double maxRevenue = 0;
        String mostProfitableSong = "";
        for (MonetizedSong monetizedSong : artist.getMonetizedSongs()) {
            if (monetizedSong.getRevenue() > maxRevenue) {
                maxRevenue = monetizedSong.getRevenue();
                mostProfitableSong = monetizedSong.getSongPlayInfo().getSong().getName();
            }
        }
        return mostProfitableSong;
    }

    /**
     * Gets monetization data for an artist.
     *
     * @param artist The artist for whom monetization data is retrieved.
     * @return An ObjectNode containing monetization data.
     */
    public ObjectNode getDataForArtist(final Artist artist) {
        ObjectNode artistData = JsonNodeFactory.instance.objectNode();
        artistData.put("merchRevenue", artist.getMerchRevenue());
        artistData.put("songRevenue", Math.round(artist.getSongRevenue() * HUNDRED) / HUNDRED);
        artistData.put("ranking", artist.getRanking() + 1);

        if (artist.getSongRevenue() == 0) {
            artistData.put("mostProfitableSong", "N/A");
        } else {
            artistData.put("mostProfitableSong", getMostProfitableSongName(artist));
        }

        return artistData;
    }

    /**
     * Updates monetization statistics for all artists.
     */
    public void updateAllArtistsMonetization() {
        for (NormalUser user : Main.normalUserList) {
            updateStats(user);
            if (user.getIsPremium()) {
                user.updateArtistsSongRevenues();
            }
        }
    }

    /**
     * Executes the end program command, updating monetization data and generating the result.
     *
     * @return The result of the command as an ObjectNode.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        updateAllArtistsMonetization();

        ObjectNode result = JsonNodeFactory.instance.objectNode();

        ArrayList<Artist> eligibleArtists = new ArrayList<>();
        for (Artist artist : Main.artistsList) {
            // check if the artist had anything on play
            if (artist.getHadSomethingOnPlay() || artist.getMerchRevenue() != 0) {
                eligibleArtists.add(artist);
            }
        }

        // get eligible artists in the correct order
        reorderEligibleArtists(eligibleArtists);

        for (Artist eligibleArtist : eligibleArtists) {
            eligibleArtist.setRanking(eligibleArtists.indexOf(eligibleArtist));
            result.put(eligibleArtist.getUsername(), getDataForArtist(eligibleArtist));
        }

        node.putPOJO("result", result);
        return node;
    }
}
