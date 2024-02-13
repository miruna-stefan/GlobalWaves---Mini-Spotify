package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.NormalUser;

import java.util.ArrayList;

public final class GetOnlineUsers extends StandardStatisticsCommand {
    private static GetOnlineUsers instance = null;

    private GetOnlineUsers(final String command, final ObjectNode node, final Integer timestamp) {
        super(command, timestamp, node);
    }

    /**
     * Gets the singleton instance of `GetOnlineUsers` with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param node      the JSON node associated with the command.
     * @param timestamp the timestamp of the command.
     * @return the singleton instance of `GetOnlineUsers`.
     */
    public static GetOnlineUsers getInstance(final String command, final ObjectNode node,
                                             final Integer timestamp) {
        if (instance == null) {
            instance = new GetOnlineUsers(command, node, timestamp);
        } else {
            instance.setCommand(command);
            instance.setNode(node);
            instance.setTimestamp(timestamp);
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
        node.put("timestamp", this.getTimestamp());
        ArrayList<String> result = new ArrayList<>();
        for (NormalUser user : Main.normalUserList) {
            if (user.getConnectionStatus()) {
                result.add(user.getUsername());
            }
        }
        node.putPOJO("result", result);
        return node;
    }

    /**
     * Gets the singleton instance of `GetOnlineUsers`.
     *
     * @return the singleton instance of `GetOnlineUsers`.
     */
    public static GetOnlineUsers getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of `GetOnlineUsers`.
     *
     * @param instance the singleton instance of `GetOnlineUsers`.
     */
    public static void setInstance(final GetOnlineUsers instance) {
        GetOnlineUsers.instance = instance;
    }
}
