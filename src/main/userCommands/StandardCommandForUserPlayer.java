package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.commandsHandling.GeneralCommand;
import users.Artist;
import users.Host;
import users.NormalUser;

public abstract class StandardCommandForUserPlayer extends GeneralCommand {
    protected String username;

    public StandardCommandForUserPlayer(final String command, final Integer timestamp,
                                 final ObjectNode node, final String username) {
        super(command, timestamp, node);
        this.username = username;
    }

    /**
     * Checks the existence of the username and its type (normal user, artist, host).
     *
     * @return A message indicating the existence and type of the username.
     */
    protected String checkUsernameExistence() {
        // look for the username in the artists' list
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.username)) {
                return this.username + " is not a normal user.";
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
     * @return The username associated with the command.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the command.
     *
     * @param username The new username associated with the command.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Checks if a user with the specified username is online.
     *
     * @param usernameToCheck The username to check for online status.
     * @return True if the user is online, false otherwise.
     */
    public Boolean isUserOnline(final String usernameToCheck) {
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(usernameToCheck)) {
                return user.getConnectionStatus();
            }
        }
        return false;
    }
}
