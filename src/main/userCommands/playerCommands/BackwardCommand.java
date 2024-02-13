package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import fileio.input.audioEntities.PodcastPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class BackwardCommand extends StandardCommandForUserPlayer {
    private static final int BACKWARD_SECONDS = 90;
    private static final int TYPE_PODCAST = 2;

    // Singleton instance field
    private static BackwardCommand instance = null;

    /**
     * Constructs a new BackwardCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    private BackwardCommand(final String command, final String username,
                            final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of BackwardCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of BackwardCommand.
     */
    public static BackwardCommand getInstance(final String command, final String username,
                                              final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new BackwardCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the backward command.
     *
     * @return The ObjectNode containing information about the backward command.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        if (!this.isUserOnline(this.getUsername())) {
            node.put("message", this.getUsername() + " is offline.");
            return node;
        }
        node.put("message", this.getMessage());
        return node;
    }


    /**
     * Executes the backward command on the user's loaded podcast.
     *
     * <p>The method checks if the podcast is currently playing, updates the playback status,
     * and rewinds the podcast by a predefined time (90 seconds) or sets the playback to the
     * beginning of the same episode if the remaining time is less than the predefined time.
     *
     * @param user The user for whom the backward command is executed.
     */
    public void executeCommand(final NormalUser user) {
        PodcastPlayInfo podcastPlayInfo = user.getLastLoadedPodcast();
        Integer currentSecondEpisode = podcastPlayInfo.getCurrentSecondEpisode();
        if (currentSecondEpisode - BACKWARD_SECONDS >= 0) {
            podcastPlayInfo.setCurrentSecondEpisode(currentSecondEpisode - BACKWARD_SECONDS);
        } else {
            podcastPlayInfo.setCurrentSecondEpisode(0);
        }
    }

    /**
     * Retrieves the message indicating the result of the backward command.
     *
     * <p>The method checks if a source is loaded, if the loaded source is a podcast,
     * and if the podcast is currently playing. It updates the podcast status,
     * executes the backward command, and returns the appropriate message.
     *
     * @return The message indicating the result of the backward command.
     */
    public String getMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before skipping backward.";
                }
                if (user.getLastSearchTypeIndicator() != TYPE_PODCAST) {
                    return "The loaded source is not a podcast.";
                }
                executeCommand(user);
            }
        }
        return "Rewound successfully.";
    }


}
