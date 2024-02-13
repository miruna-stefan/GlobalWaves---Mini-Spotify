package main.hostCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.audioEntities.PodcastInput;
import main.Main;
import users.Host;

public final class RemovePodcast extends StandardHostCommand {

    // Singleton instance field
    private static RemovePodcast instance = null;
    private RemovePodcast(final String command, final String username, final Integer timestamp,
                          final String name, final ObjectNode node) {
        super(command, username, timestamp, name, node);
    }

    /**
     * Gets the singleton instance of RemovePodcast.
     *
     * @param command   The command associated with this command.
     * @param username  The username associated with this command.
     * @param timestamp The timestamp of the command.
     * @param name      The name of the podcast to be removed.
     * @param node      The JSON node associated with this command.
     * @return The singleton instance of RemovePodcast.
     */
    public static RemovePodcast getInstance(final String command, final String username,
                                            final Integer timestamp, final String name,
                                            final ObjectNode node) {
        if (instance == null) {
            instance = new RemovePodcast(command, username, timestamp, name, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
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
     * Executes the RemovePodcast command, removing the podcast
     * associated with the command's parameters.
     *
     * @return A string indicating the result of the command execution.
     */
    public String getCommandMessage() {
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.username)) {
                // this host has already been created => the command can be executed

                // check if the host has a podcast with the given name
                PodcastInput podcastToRemove = null;
                for (PodcastInput podcast : host.getPodcasts()) {
                    if (podcast.getName().equals(this.name)) {
                        podcastToRemove = podcast;
                        break;
                    }
                }
                if (podcastToRemove == null) {
                    return this.username + " doesn't have a podcast with the given name.";
                }

                // check if the podcast can be deleted
                if (!host.canDeletePodcast(podcastToRemove, this.timestamp)) {
                    return this.username + " can't delete this podcast.";
                }

                // if we reached this point, it means that we need to delete this podcast
                host.preparePodcastRemoval(podcastToRemove);
                // remove podcast from this host's list of podcasts
                for (PodcastInput podcast : host.getPodcasts()) {
                    if (podcast.getName().equals(this.name)) {
                        host.getPodcasts().remove(podcast);
                        return host.getUsername() + " deleted the podcast successfully.";
                    }
                }
            }
        }

        // if we reached this point, it means that the username doesn't appear in the host list
        return checkUsernameExistence();
    }


}
