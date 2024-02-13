package main.adminCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.commandsHandling.GeneralCommand;


public abstract class StandardAdminCommand extends GeneralCommand {
    private String username;

    public StandardAdminCommand(final String command, final String username,
                                final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node);
        this.username = username;
    }

    /**
     * Gets the username associated with the admin command.
     *
     * @return the username associated with the admin command.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the admin command.
     *
     * @param username the username to be set for the admin command.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

}
