package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.NormalUser;

import java.util.ArrayList;

public final class BuyPremium extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static BuyPremium instance = null;

    private BuyPremium(final String command, final String username,
                       final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of BuyPremium.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of BuyPremium.
     */
    public static BuyPremium getInstance(final String command, final String username,
                                         final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new BuyPremium(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the command message indicating the result of the premium subscription purchase.
     *
     * @return A String message indicating the result of the premium subscription purchase.
     */
    public String getCommandMessage() {
        NormalUser user = null;
        for (NormalUser normalUser : Main.normalUserList) {
            if (normalUser.getUsername().equals(this.getUsername())) {
                user = normalUser;
                break;
            }
        }

        if (user == null) {
            return "The username " + this.getUsername() + " doesn't exist.";
        }

        // check if the user is already premium
        if (user.getIsPremium()) {
            return this.getUsername() + " is already a premium user.";
        }

        /* if we reached this point, it means that the user is not
        premium, so we need to make it premium */
        user.setIsPremium(true);
        user.setSongsPlayedWhilePremium(new ArrayList<>());
        user.setArtistsPlayedWhilePremium(new ArrayList<>());
        return this.getUsername() + " bought the subscription successfully.";

    }

    /**
     * Executes the BuyPremium command.
     *
     * @return The JSON node containing the results of the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", getCommandMessage());
        return node;
    }
}
