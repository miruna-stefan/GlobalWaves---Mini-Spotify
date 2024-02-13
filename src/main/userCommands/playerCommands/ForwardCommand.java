package main.userCommands.playerCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.EpisodeInput;
import main.Main;
import fileio.input.audioEntities.PodcastPlayInfo;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class ForwardCommand extends StandardCommandForUserPlayer {
    private static final int FORWARD_SECONDS = 90;
    private static final int TYPE_PODCAST = 2;

    // Singleton instance field
    private static ForwardCommand instance = null;

    /**
     * Constructs a new ForwardCommand with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    // make constructor private for singleton implementation
    private ForwardCommand(final String command, final String username,
                           final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of ForwardCommand.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of ForwardCommand.
     */
    public static ForwardCommand getInstance(final String command, final String username,
                                             final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new ForwardCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the forward command.
     *
     * @return The ObjectNode containing information about the forward command.
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
     * Executes the forward command on the user's loaded podcast.
     *
     * @param user The user for whom the forward command is executed.
     */
    public void executeCommand(final NormalUser user) {
        PodcastPlayInfo podcastPlayInfo = user.getLastLoadedPodcast();
        Integer currentEpisodeIndex = podcastPlayInfo.getCurrentEpisodeIndex();
        EpisodeInput episode = podcastPlayInfo.getPodcast().getEpisodes().get(currentEpisodeIndex);
        Integer currentEpisodeDuration = episode.getDuration();
        Integer currentSecondEpisode = podcastPlayInfo.getCurrentSecondEpisode();
        if (currentSecondEpisode + FORWARD_SECONDS <= currentEpisodeDuration) {
            podcastPlayInfo.setCurrentSecondEpisode(currentSecondEpisode + FORWARD_SECONDS);
        } else {
            user.getLastLoadedPodcast().setCurrentEpisodeIndex(currentEpisodeIndex + 1);
            user.getLastLoadedPodcast().setCurrentSecondEpisode(0);
        }
    }

    /**
     * Retrieves the message indicating the result of the forward command.
     *
     * @return The message indicating the result of the forward command.
     */
    public String getMessage() {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                if (!user.stillHasSomethingLoaded(this.timestamp)) {
                    return "Please load a source before attempting to forward.";
                }
                if (user.getLastSearchTypeIndicator() != TYPE_PODCAST) {
                    return "The loaded source is not a podcast.";
                }
                executeCommand(user);
            }
        }
        return "Skipped forward successfully.";
    }


}
