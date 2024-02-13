package main.artistCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandsHandling.GeneralCommand;
import main.Main;
import users.Host;
import users.NormalUser;

public abstract class StandardArtistCommand extends GeneralCommand {
    private String username;
    private String name;

    public StandardArtistCommand(final String command, final String username,
                                 final Integer timestamp, final String name,
                                 final ObjectNode node) {
        super(command, timestamp, node);
        this.username = username;
        this.name = name;
    }

    /**
     * Checks if the username exists and is not an artist.
     *
     * @return a message indicating whether the username exists and is not an artist.
     */
    protected String checkUsernameExistence() {
        // look for the username in the normal users' list
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.username)) {
                return this.username + " is not an artist.";
            }
        }

        // look for the username in the hosts' list
        for (Host host : Main.hostsList) {
            if (host.getUsername().equals(this.username)) {
                return this.username + " is not an artist.";
            }
        }

        // if we reached this point, it means that the user could not be found in any list
        return "The username " + this.username + " doesn't exist.";
    }


    /**
     * Gets the username associated with the command.
     *
     * @return the username associated with the command.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the command.
     *
     * @param username the username to set.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the name associated with the command.
     *
     * @return the name associated with the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with the command.
     *
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

}
