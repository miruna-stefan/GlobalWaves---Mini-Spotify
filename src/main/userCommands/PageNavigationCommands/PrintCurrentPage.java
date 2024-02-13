package main.userCommands.PageNavigationCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import main.userCommands.StandardCommandForUserPlayer;
import users.NormalUser;

import java.util.ArrayList;

public final class PrintCurrentPage extends StandardCommandForUserPlayer {
    private static final int TYPE_ARTIST = 5;
    private static final int TYPE_HOST = 6;
    private static final int MAX_RESULTS = 5;
    private static PrintCurrentPage instance = null;

    /**
     * Constructs a new PrintCurrentPage command with the specified parameters.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     */
    private PrintCurrentPage(final String command, final String username, final Integer timestamp,
                             final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of PrintCurrentPage.
     *
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of PrintCurrentPage.
     */
    public static PrintCurrentPage getInstance(final String command, final String username,
                                               final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new PrintCurrentPage(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the print current page command.
     *
     * @return The ObjectNode containing information about the print current page operation.
     */
    public ObjectNode execute() {
        node.put("user", this.getUsername());
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());

        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                // check if the user is online
                if (!user.getConnectionStatus()) {
                    node.put("message", this.getUsername() + " is offline.");
                    ArrayList<String> result = new ArrayList<>();
                    return node;
                }

                node.put("message", user.getPageHistory().
                        get(user.getCurrentPageIndex()).pageToString());
            }
        }
        return node;
    }
}
