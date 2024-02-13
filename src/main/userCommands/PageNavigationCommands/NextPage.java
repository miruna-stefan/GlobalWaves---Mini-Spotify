package main.userCommands.PageNavigationCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

public final class NextPage extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static NextPage instance = null;

    private NextPage(final String command, final String username, final Integer timestamp,
                     final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of NextPage.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of NextPage.
     */
    public static NextPage getInstance(final String command, final String username,
                                       final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new NextPage(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the command message indicating the result of the navigation to the next page.
     *
     * @param user The user for whom the navigation is performed.
     * @return A String message indicating the result of the navigation to the next page.
     */
    public String getCommandExecutionMessage(final NormalUser user) {
        // check if there is any next page in the history
        if (user.getCurrentPageIndex() == user.getPageHistory().size() - 1) {
            return "There are no pages left to go forward.";
        }

        user.setCurrentPageIndex(user.getCurrentPageIndex() + 1);
        return "The user " + this.getUsername() + " has navigated successfully to the next page.";
    }

    /**
     * Executes the NextPage command.
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
                break;
            }
        }
        return node;
    }
}
