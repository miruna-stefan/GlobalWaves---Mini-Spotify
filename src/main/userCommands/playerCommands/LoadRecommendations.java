package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class LoadRecommendations extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static LoadRecommendations instance = null;
    private static final int TYPE_SONG = 1;

    private LoadRecommendations(final String command, final String username,
                                final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of LoadRecommendations.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of LoadRecommendations.
     */
    public static LoadRecommendations getInstance(final String command, final String username,
                                                  final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new LoadRecommendations(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the command execution message for loading recommendations.
     *
     * @param user The user associated with the playback.
     * @return A String message indicating the result of the load operation.
     */
    public String getCommandExecutionMessage(final NormalUser user) {
        // check if the user is online
        if (!user.getConnectionStatus()) {
            return this.getUsername() + " is offline.";
        }

        // check if the user has any recommendations
        if (user.getSongRecommendations().isEmpty()
                && user.getFansPlaylistRecommendations() == null
                && user.getRandomPlaylistRecommendation() == null) {
            return "No recommendations available.";
        }

        // load the recommendations
        if (user.getLastRecommendationType().equals("random_song")) {
            user.setLastLoadTypeIndicator(TYPE_SONG);
            user.setLastLoadedSongPlayInfo(user.getLastSongRecommendation());
            user.getLastLoadedSongPlayInfo().setLastPlayTimestamp(this.timestamp);
            user.getLastLoadedSongPlayInfo().setRepeatStatus(0);
            user.getLastLoadedSongPlayInfo().setSongPaused(false);
            user.getLastLoadedSongPlayInfo().setCurrentSecond(0);
            user.setLastSelectedSong(null);
            user.updateEverythingForSong(user.getLastLoadedSongPlayInfo());

            updateArtistHadSomethingOnPlay(user.getLastLoadedSongPlayInfo());
            user.setLoaded(true);
            user.setSelected(false);
            user.setSearched(false);
        }

        return "Playback loaded successfully.";
    }

    /**
     * Executes the LoadRecommendations command.
     *
     * @return The JSON node containing the results of the command execution.
     */
    @Override
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                node.put("message", getCommandExecutionMessage(user));
                break;
            }
        }
        return node;
    }
}
