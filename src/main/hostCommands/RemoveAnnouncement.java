package main.hostCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForHost.Announcement;
import main.Main;
import users.Host;

public final class RemoveAnnouncement extends StandardHostCommand {
    private static RemoveAnnouncement instance = null;

    /**
     * Constructs a new RemoveAnnouncement command with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the command.
     * @param node      the JSON node associated with the command.
     */
    private RemoveAnnouncement(final String command, final String username, final Integer timestamp,
                               final String name, final ObjectNode node) {
        super(command, username, timestamp, name, node);
    }

    /**
     * Gets the singleton instance of RemoveAnnouncement.
     *
     * @param command   the command associated with the command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of RemoveAnnouncement.
     */
    public static RemoveAnnouncement getInstance(final String command, final String username,
                                                 final Integer timestamp, final String name,
                                                 final ObjectNode node) {
        if (instance == null) {
            instance = new RemoveAnnouncement(command, username, timestamp, name, node);
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
     * Executes the RemoveAnnouncement command, removing the announcement
     * associated with the command's parameters.
     *
     * @return A string indicating the result of the command execution.
     */
    public String getCommandMessage() {
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.username)) {
                // this host has already been created => the command can be executed

                // check if the host has any announcement with the given name
                Boolean found = false;
                for (Announcement announcement : host.getAnnouncements()) {
                    if (announcement.getName().equals(this.name)) {
                        found = true;
                        host.getAnnouncements().remove(announcement);
                        break;
                    }
                }
                if (!found) {
                    return this.username + " has no announcement with the given name.";
                }

                return this.username + " has successfully deleted the announcement.";
            }
        }
        return checkUsernameExistence();
    }


}
