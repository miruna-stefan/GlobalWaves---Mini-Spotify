package main.artistCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForArtist.Event;
import main.Main;
import users.Artist;

public final class RemoveEvent extends StandardArtistCommand {

    // Singleton instance field
    private static RemoveEvent instance = null;

    private RemoveEvent(final String command, final String username, final Integer timestamp,
                        final String name, final ObjectNode node) {
        super(command, username, timestamp, name, node);
    }

    /**
     * Gets the singleton instance of RemoveEvent.
     *
     * @param command   the command string.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param name      the name of the event to be removed.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of RemoveEvent.
     */
    public static RemoveEvent getInstance(final String command, final String username,
                                          final Integer timestamp, final String name,
                                          final ObjectNode node) {
        if (instance == null) {
            instance = new RemoveEvent(command, username, timestamp, name, node);
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
     *
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
     * Executes the remove event command.
     *
     * @return a message indicating the success or failure of the command.
     */
    public String getCommandMessage() {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // this artist has already been created => the command can be executed

                // check if the artist has any event with the given name
                Event eventToBeDeleted = null;
                for (Event event : artist.getEvents()) {
                    if (event.getName().equals(this.getName())) {
                        eventToBeDeleted = event;
                        break;
                    }
                }

                if (eventToBeDeleted == null) {
                    return this.getUsername() + " doesn't have an event with the given name.";
                }

                // remove the event from the artist's list of events
                artist.getEvents().remove(eventToBeDeleted);
                return this.getUsername() + " deleted the event successfully.";
            }
        }

        return checkUsernameExistence();
    }

}
