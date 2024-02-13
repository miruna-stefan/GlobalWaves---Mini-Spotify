package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.NormalUser;

public final class CancelPremium extends StandardCommandForUserPlayer {
    // Singleton instance field
    private static CancelPremium instance = null;

    private CancelPremium(final String command, final String username,
                          final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of CancelPremium.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of CancelPremium.
     */
    public static CancelPremium getInstance(final String command, final String username,
                                             final Integer timestamp, final ObjectNode node) {
        if (instance == null) {
            instance = new CancelPremium(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the command execution message for canceling a premium subscription.
     *
     * @return A String message indicating the result of canceling the premium subscription.
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

        // check if the user is even premium
        if (!user.getIsPremium()) {
            return this.getUsername() + " is not a premium user.";
        }

        updateStats(user);
        user.updateArtistsSongRevenues();
        user.setIsPremium(false);

        return this.getUsername() + " cancelled the subscription successfully.";
    }

    /**
     * Executes the CancelPremium command.
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
