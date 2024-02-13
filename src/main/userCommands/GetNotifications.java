package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.NormalUser;

import java.util.ArrayList;

public final class GetNotifications extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static GetNotifications instance = null;

    private GetNotifications(final String command, final String username,
                             final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of GetNotifications.
     *
     * @param command   the command string indicating the type of command.
     * @param username  the username associated with the command.
     * @param timestamp the timestamp of the command.
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of GetNotifications.
     */
    public static GetNotifications getInstance(final String command, final String username,
                                               final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new GetNotifications(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     *
     * @return The ObjectNode containing information about the get notifications operation.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        NormalUser user = null;
        for (NormalUser normalUser : Main.normalUserList) {
            if (normalUser.getUsername().equals(this.getUsername())) {
                user = normalUser;
                break;
            }
        }
        if (user == null) {
            node.put("message", this.getUsername() + " doesn't exist.");
            return node;
        }

        ArrayList<ObjectNode> notifications = new ArrayList<>();
        notifications.addAll(user.getNotifications());
        node.putPOJO("notifications", notifications);

        // delete all notifications after printing them
        user.resetNotifications();

        return node;
    }
}
