package main.statisticsCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;

import java.util.ArrayList;

public final class GetAllUsers extends StandardStatisticsCommand {
    private static GetAllUsers instance = null;

    private GetAllUsers(final String command, final ObjectNode node, final Integer timestamp) {
        super(command, timestamp, node);
    }

    /**
     * Gets the singleton instance of `GetAllUsers` with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param node      the JSON node associated with the command.
     * @param timestamp the timestamp of the command.
     * @return the singleton instance of `GetAllUsers`.
     */
    public static GetAllUsers getInstance(final String command, final ObjectNode node,
                                          final Integer timestamp) {
        if (instance == null) {
            instance = new GetAllUsers(command, node, timestamp);
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
        node.putPOJO("result", this.getListOfAllUsersNames());
        return node;
    }

    /**
     * Retrieves a list of all usernames (normal users, artists, and hosts).
     *
     * @return the list of all usernames.
     */
    public ArrayList<String> getListOfAllUsersNames() {
        ArrayList<String> result = new ArrayList<>();
        for (NormalUser user : Main.normalUserList) {
            result.add(user.getUsername());
        }
        for (Artist artist : Main.artistsList) {
            result.add(artist.getUsername());
        }
        for (Host host : Main.hostsList) {
            result.add(host.getUsername());
        }
        return result;
    }

    /**
     * Gets the singleton instance of `GetAllUsers`.
     *
     * @return the singleton instance of `GetAllUsers`.
     */
    public static GetAllUsers getInstance() {
        return instance;
    }

    /**
     * Sets the singleton instance of `GetAllUsers`.
     *
     * @param instance the singleton instance of `GetAllUsers`.
     */
    public static void setInstance(final GetAllUsers instance) {
        GetAllUsers.instance = instance;
    }

}
