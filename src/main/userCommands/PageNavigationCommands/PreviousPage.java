package main.userCommands.PageNavigationCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class PreviousPage extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static PreviousPage instance = null;

    private PreviousPage(final String command, final String username, final Integer timestamp,
                         final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of PreviousPage.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of PreviousPage.
     */
    public static PreviousPage getInstance(final String command, final String username,
                                           final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new PreviousPage(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the command execution message for navigating to the previous page.
     *
     * @param user The user associated with the navigation.
     * @return A String message indicating the result of the navigation.
     */
    public String getCommandExecutionMessage(final NormalUser user) {
        // check if there is any previous page in the history
        if (user.getCurrentPageIndex() == 0) {
            return "There are no pages left to go back.";
        }
        user.setCurrentPageIndex(user.getCurrentPageIndex() - 1);
        return "The user " + this.getUsername()
                + " has navigated successfully to the previous page.";
    }

    /**
     * Executes the PreviousPage command.
     *
     * @return The JSON node containing the results of the command execution.
     */
    @Override
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                node.put("message", getCommandExecutionMessage(user));
            }
        }

        return node;
    }
}
