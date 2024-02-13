package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import fileio.input.audioEntities.SongPlayInfo;

import java.util.ArrayList;

public final class GetTop5SongsCommand extends StandardStatisticsCommand {

    private static final int MAX_RESULTS = 5;
    private ArrayList<SongPlayInfo> songs;

    // Singleton instance field
    private static GetTop5SongsCommand instance = null;

    /**
     * Constructs a new GetTop5SongsCommand with the specified parameters.
     *
     * @param songs      The list of songs for which to retrieve top likes.
     * @param timestamp  The timestamp of the command.
     * @param node       The JSON node associated with the command.
     */
    // make constructor private for singleton implementation
    private GetTop5SongsCommand(final String command, final ArrayList<SongPlayInfo> songs,
                                final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node);
        this.songs = songs;
    }

    /**
     * Gets the singleton instance of GetTop5SongsCommand.
     *
     * @param songs      The list of songs for which to retrieve top likes.
     * @param timestamp  The timestamp of the command.
     * @param node       The JSON node associated with the command.
     * @return The singleton instance of GetTop5SongsCommand.
     */
    public static GetTop5SongsCommand getInstance(final String command,
                                                  final ArrayList<SongPlayInfo> songs,
                                                  final Integer timestamp,
                                                  final ObjectNode node) {
        if (instance == null) {
            instance = new GetTop5SongsCommand(command, songs, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setSongs(songs);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the top 5 songs based on likes.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());
        ArrayList<SongPlayInfo> sortedSongs = this.getSortedSongsByLikes(Main.songsList);
        node.putPOJO("result", this.getSongNamesStrings(getTruncatedResultTop5Songs(sortedSongs)));
        return node;
    }


    /**
     * Retrieves the list of songs for which to retrieve top likes.
     *
     * @return The list of songs.
     */
    public ArrayList<SongPlayInfo> getSongs() {
        return songs;
    }

    /**
     * Sets the list of songs for which to retrieve top likes.
     *
     * @param songs The list of songs to set.
     */
    public void setSongs(final ArrayList<SongPlayInfo> songs) {
        this.songs = songs;
    }

    public static GetTop5SongsCommand getInstance() {
        return instance;
    }

    public static void setInstance(final GetTop5SongsCommand instance) {
        GetTop5SongsCommand.instance = instance;
    }

}
