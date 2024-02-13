package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForArtist.Merch;
import main.Main;
import users.NormalUser;

import java.util.ArrayList;

public final class SeeMerch extends StandardCommandForUserPlayer {
    private static SeeMerch instance = null;

    private SeeMerch(final String command, final String username,
                    final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of the SeeMerch class.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp associated with the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of the SeeMerch class.
     */
    public static SeeMerch getInstance(final String command, final String username,
                                        final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new SeeMerch(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves and formats the names of bought merch items for a NormalUser.
     *
     * @param user The NormalUser for whom bought merch items names are retrieved.
     * @return An ArrayList of strings containing the names of bought merch items.
     */
    public ArrayList<String> getBoughtMerchItemsNames(final NormalUser user) {
        ArrayList<String> merchNames = new ArrayList<>();
        for (Merch merch : user.getBoughtMerch()) {
            merchNames.add(merch.getName());
        }
        return merchNames;
    }

    /**
     * Executes the see merch command.
     *
     * @return The result of the command as an ObjectNode.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        // look for the username
        NormalUser user = null;
        // check if the username exists
        for (NormalUser normalUser : Main.normalUserList) {
            if (normalUser.getUsername().equals(this.getUsername())) {
                user = normalUser;
            }
        }
        if (user == null) {
            node.put("message", "The username " + this.getUsername() + " doesn't exist.");
            return node;
        }

        node.putPOJO("result", this.getBoughtMerchItemsNames(user));
        return node;
    }
}
