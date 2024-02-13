package main.hostCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandsHandling.GeneralCommand;
import main.Main;
import users.Artist;
import users.NormalUser;

public abstract class StandardHostCommand extends GeneralCommand {
    protected String username;
    protected String name;

    public StandardHostCommand(final String command, final String username, final Integer timestamp,
                               final String name, final ObjectNode node) {
        super(command, timestamp, node);
        this.username = username;
        this.name = name;
    }

    /**
     * Checks if the username exists and is not a host.
     *
     * @return A string indicating whether the username is not a host or does not exist.
     */
    // check if the username exists, but it's not a host
    protected String checkUsernameExistence() {
        // look for the username in the normal users' list
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                return this.username + " is not a host.";
            }
        }

        // look for the username in the hosts' list
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.username)) {
                return this.username + " is not a host.";
            }
        }

        // if we reached this point, it means that the user could not be found in any list
        return "The username " + this.username + " doesn't exist.";
    }


    /**
     * Gets the username associated with this command.
     *
     * @return The username associated with this command.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with this command.
     *
     * @param username The new username associated with this command.
     */
    public void setUsername(final String username) {
        this.username = username;
    }


    /**
     * Gets the name associated with this command.
     *
     * @return The name associated with this command.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with this command.
     *
     * @param name The new name associated with this command.
     */
    public void setName(final String name) {
        this.name = name;
    }

}
