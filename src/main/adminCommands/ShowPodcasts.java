package main.adminCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.PodcastInput;
import main.Main;
import users.Host;

import java.util.ArrayList;

public final class ShowPodcasts extends StandardAdminCommand {

    // Singleton instance field
    private static ShowPodcasts instance = null;
    private ShowPodcasts(final String command, final String username,
                         final Integer timestamp, final ObjectNode node) {
        super(command, username, timestamp, node);
    }

    /**
     * Gets the singleton instance of ShowPodcasts.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The ShowPodcasts instance.
     */
    public static ShowPodcasts getInstance(final String command, final String username,
                                           final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new ShowPodcasts(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Gets the names of episodes in the given podcast.
     *
     * @param podcastInput The podcast for which to retrieve episode names.
     * @return The list of episode names.
     */
    public ArrayList<String> getEpisodesNames(final PodcastInput podcastInput) {
        ArrayList<String> episodesNames = new ArrayList<>();
        for (int i = 0; i < podcastInput.getEpisodes().size(); i++) {
            episodesNames.add(podcastInput.getEpisodes().get(i).getName());
        }
        return episodesNames;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        ArrayList<ObjectNode> result = new ArrayList<>();
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.getUsername())) {
                for (PodcastInput podcastInput : host.getPodcasts()) {
                    ObjectNode podcastNode = JsonNodeFactory.instance.objectNode();
                    podcastNode.put("name", podcastInput.getName());
                    podcastNode.putPOJO("episodes", this.getEpisodesNames(podcastInput));
                    result.add(podcastNode);
                }
                node.putPOJO("result", result);
            }
        }

        return node;
    }
}
