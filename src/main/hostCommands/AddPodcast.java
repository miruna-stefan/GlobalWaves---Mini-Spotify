package main.hostCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.PodcastInput;
import main.Main;
import users.Host;

import java.util.ArrayList;

public final class AddPodcast extends StandardHostCommand {
    private ArrayList<EpisodeInput> episodes;

    // Singleton instance field
    private static AddPodcast instance = null;

    /**
     * Constructs a new AddPodcast command with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the podcast.
     * @param node      the JSON node associated with the command.
     * @param episodes  the list of episodes for the podcast.
     */
    private AddPodcast(final String command, final String username, final Integer timestamp,
                       final String name, final ObjectNode node,
                       final ArrayList<EpisodeInput> episodes) {
        super(command, username, timestamp, name, node);
        this.episodes = episodes;
    }

    /**
     * Gets the singleton instance of AddPodcast.
     *
     * @param command   the command associated with the command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the podcast.
     * @param node      the JSON node associated with the command.
     * @param episodes  the list of episodes for the podcast.
     * @return the singleton instance of AddPodcast.
     */
    public static AddPodcast getInstance(final String command, final String username,
                                         final Integer timestamp, final String name,
                                         final ObjectNode node,
                                         final ArrayList<EpisodeInput> episodes) {
        if (instance == null) {
            instance = new AddPodcast(command, username, timestamp, name, node, episodes);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
            instance.setEpisodes(episodes);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getCommandMessage());
        return node;
    }

    /**
     * Checks if there are duplicates in the list of episodes.
     *
     * @param albumEpisodes the list of episodes to check for duplicates.
     * @return true if there are duplicates, false otherwise.
     */
    private Boolean hasDuplicates(final ArrayList<EpisodeInput> albumEpisodes) {
        for (int i = 0; i < albumEpisodes.size(); i++) {
            for (int j = i + 1; j < albumEpisodes.size(); j++) {
                if (albumEpisodes.get(i).getName().equals(albumEpisodes.get(j).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Executes the AddPodcast command, adding a new podcast to the host's list.
     *
     * @return a message indicating the result of the command execution.
     */
    public String getCommandMessage() {
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.username)) {
                // the given username belongs to a host

                // check if the podcast name is already taken
                for (PodcastInput podcast : host.getPodcasts()) {
                    if (podcast.getName().equals(this.name)) {
                        return this.username + " has another podcast with the same name.";
                    }
                }

                // check if the podcast has duplicates in the episode list
                if (hasDuplicates(this.episodes)) {
                    return this.username + " has the same episode in this podcast.";
                }

                // if the episodelist is valid, create the podcast and add it to the list
                PodcastInput podcastInput = new PodcastInput();
                podcastInput.setName(this.name);
                podcastInput.setOwner(this.username);
                podcastInput.setEpisodes(this.episodes);
                host.getPodcasts().add(podcastInput);

                // add the podcast to the list of all podcasts
                Main.podcastsList.add(podcastInput);

                // prepare the notification message
                ObjectNode newNotification = JsonNodeFactory.instance.objectNode();
                newNotification.put("name", "New Podcast");
                newNotification.put("description", "New Podcast from " + host.getUsername() + ".");
                host.notifyObservers(newNotification);

                return this.username + " has added new podcast successfully.";
            }
        }
        // if we reached this point, it means that the username doesn't appear in the artist list
        return checkUsernameExistence();
    }


    /**
     * Gets the list of episodes for the podcast.
     *
     * @return the list of episodes.
     */
    public ArrayList<EpisodeInput> getEpisodes() {
        return episodes;
    }

    /**
     * Sets the list of episodes for the podcast.
     *
     * @param episodes the list of episodes to set.
     */
    public void setEpisodes(final ArrayList<EpisodeInput> episodes) {
        this.episodes = episodes;
    }
}
