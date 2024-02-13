package main.hostCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForHost.Announcement;
import main.Main;
import users.Host;

public final class AddAnnouncement extends StandardHostCommand {
    private String description;

    // generate singleton constructor
    private static AddAnnouncement instance = null;

    /**
     * Constructs a new AddAnnouncement command with the specified parameters.
     *
     * @param command    the command associated with the command.
     * @param username   the username associated with the command.
     * @param timestamp  the timestamp of the command.
     * @param name       the name of the command.
     * @param node       the JSON node associated with the command.
     * @param description the description of the announcement
     */
    private AddAnnouncement(final String command, final String username, final Integer timestamp,
                            final String name, final ObjectNode node, final String description) {
        super(command, username, timestamp, name, node);
        this.description = description;
    }

    /**
     * Gets the singleton instance of AddAnnouncement.
     *
     * @param command    the command associated with the command.
     * @param username   the username associated with the command.
     * @param timestamp  the timestamp of the command.
     * @param name       the name of the command.
     * @param node       the JSON node associated with the command.
     * @param description the description of the announcement
     * @return the singleton instance of AddAnnouncement.
     */
    public static AddAnnouncement getInstance(final String command, final String username,
                                              final Integer timestamp, final String name,
                                              final ObjectNode node, final String description) {
        if (instance == null) {
            instance = new AddAnnouncement(command, username, timestamp, name, node, description);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
            instance.setDescription(description);
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
     * Executes the AddAnnouncement command.
     *
     * @return A string indicating the result of the command execution.
     */
    public String getCommandMessage() {
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.username)) {
                // this host has already been created => the command can be executed

                // check if the announcement name is already taken
                for (Announcement announcement : host.getAnnouncements()) {
                    if (announcement.getName().equals(this.name)) {
                        return this.username + " has already added an announcement with this name.";
                    }
                }

                /* if we reached this point, the validity check was passed and
                we need to add the announcement */
                Announcement newAnnouncement = new Announcement(this.name, this.description);
                host.getAnnouncements().add(newAnnouncement);

                // prepare the notification message
                ObjectNode newNotification = JsonNodeFactory.instance.objectNode();
                newNotification.put("name", "New Announcement");
                newNotification.put("description", "New Announcement from "
                        + host.getUsername() + ".");
                host.notifyObservers(newNotification);

                return this.username + " has successfully added new announcement.";
            }
        }
        return checkUsernameExistence();
    }



    /**
     * Gets the description of the announcement.
     *
     * @return The description of the announcement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the announcement.
     *
     * @param description The new description of the announcement.
     */
    public void setDescription(final String description) {
        this.description = description;
    }
}
