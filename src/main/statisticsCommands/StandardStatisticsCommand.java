package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.SongPlayInfo;
import main.commandsHandling.GeneralCommand;
import main.Main;
import users.Artist;
import users.NormalUser;
import fileio.input.wrappedEntities.WrappedArtist;
import fileio.input.wrappedEntities.WrappedFan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public abstract class StandardStatisticsCommand  extends GeneralCommand {
    private static final int MAX_RESULTS = 5;
    public StandardStatisticsCommand(final String command, final Integer timestamp,
                                     final ObjectNode node) {
        super(command, timestamp, node);
    }

    /**
     * Retrieves the list of fans for a given artist.
     *
     * @param artist The artist for which to retrieve fans.
     * @return The list of WrappedFan objects representing fans of the artist.
     */
    public ArrayList<WrappedFan> getArtistFans(final Artist artist) {
        ArrayList<WrappedFan> artistWrappedFans = new ArrayList<>();

        /* go through the wrapped artists list of each user and populate
        the wrapped fans list of the artist */
        for (NormalUser user : Main.normalUserList) {
            for (WrappedArtist userWrappedArtist : user.getWrappedArtists()) {
                if (userWrappedArtist.getArtistName().equals(artist.getUsername())) {
                    WrappedFan wrappedFan = new WrappedFan(user);
                    artistWrappedFans.add(wrappedFan);
                    wrappedFan.setListens(userWrappedArtist.getListens());
                }
            }
        }
        return artistWrappedFans;
    }

    /**
     * Retrieves the top fans from a given list of fans.
     *
     * @param wrappedFansList The list of WrappedFan objects.
     * @return The top fans in the form of a list.
     */
    public ArrayList<WrappedFan> getTopFansList(
            final ArrayList<WrappedFan> wrappedFansList) {
        /* sort the user's wrapped artist list by count of plays in descending order and
        in case of equality, sort lexicographically */
        wrappedFansList.sort(new Comparator<WrappedFan>() {
            @Override
            public int compare(final WrappedFan wrappedFan1, final WrappedFan wrappedFan2) {
                // if the number of listens is equal, sort in lexicographical order
                if (Objects.equals(wrappedFan1.getListens(), wrappedFan2.getListens())) {
                    return wrappedFan1.getNormalUser().getUsername().compareTo(wrappedFan2.
                            getNormalUser().getUsername());
                }
                return wrappedFan2.getListens() - wrappedFan1.getListens();
            }
        });

        // keep only the first 5 objects
        ArrayList<WrappedFan> topFansList = new ArrayList<>();

        // check if we have less than 5 artists
        if (wrappedFansList.size() < MAX_RESULTS) {
            for (int i = 0; i < wrappedFansList.size(); i++) {
                topFansList.add(wrappedFansList.get(i));
            }
            return topFansList;
        }

        for (int i = 0; i < MAX_RESULTS; i++) {
            topFansList.add(wrappedFansList.get(i));
        }
        return topFansList;
    }

    /**
     * Retrieves the list of top fans in string format.
     *
     * @param wrappedFansList The list of WrappedFan objects.
     * @return The list of top fans' usernames.
     */
    public ArrayList<String> getTopFansListString(
            final ArrayList<WrappedFan> wrappedFansList) {
        ArrayList<String> fansStringList = new ArrayList<>();
        for (WrappedFan wrappedFan : wrappedFansList) {
            fansStringList.add(wrappedFan.getNormalUser().getUsername());
        }
        return fansStringList;
    }

    /**
     * Retrieves the list of songs sorted by the number of likes in descending order.
     *
     * @param songList The list of SongPlayInfo objects.
     * @return The list of sorted SongPlayInfo objects.
     */
    public ArrayList<SongPlayInfo> getSortedSongsByLikes(
            final ArrayList<SongPlayInfo> songList) {
        ArrayList<SongPlayInfo> sortedSongs = new ArrayList<>();
        sortedSongs.addAll(songList);

        // sort the Songs list by the NumberOf Likes in descending order
        sortedSongs.sort(new Comparator<SongPlayInfo>() {
            @Override
            public int compare(final SongPlayInfo song1, final SongPlayInfo song2) {
                return song2.getNumberOfLikes() - song1.getNumberOfLikes();
            }
        });

        return sortedSongs;
    }

    /**
     * Retrieves the truncated result containing the top 5 songs.
     *
     * @param sortedSongs The list of sorted SongPlayInfo objects.
     * @return The truncated result containing the top 5 songs.
     */
    public static ArrayList<SongPlayInfo> getTruncatedResultTop5Songs(
            final ArrayList<SongPlayInfo> sortedSongs) {
        ArrayList<SongPlayInfo> result = new ArrayList<>();

        if (sortedSongs.size() < MAX_RESULTS) {
            for (int i = 0; i < sortedSongs.size(); i++) {
                result.add(sortedSongs.get(i));
            }
            return result;
        }


        for (int i = 0; i < MAX_RESULTS; i++) {
            result.add(sortedSongs.get(i));
        }

        return result;
    }

    /**
     * Retrieves the list of song names from the sorted songs.
     *
     * @param sortedSongs The list of sorted SongPlayInfo objects.
     * @return The list of song names.
     */
    public static ArrayList<String> getSongNamesStrings(
            final ArrayList<SongPlayInfo> sortedSongs) {
        ArrayList<String> result = new ArrayList<>();

        for (SongPlayInfo sortedSongs1 : sortedSongs) {
            result.add(sortedSongs1.getSong().getName());
        }
        return result;
    }

}
