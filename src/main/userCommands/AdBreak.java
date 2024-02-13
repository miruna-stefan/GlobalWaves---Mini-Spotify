package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class AdBreak extends StandardCommandForUserPlayer {
    private int price;

    // Singleton instance field
    private static AdBreak instance = null;

    private AdBreak(final String command, final String username, final Integer timestamp,
                    final ObjectNode node, final int price) {
        super(command, timestamp, node, username);
        this.price = price;
    }

    /**
     * Gets the singleton instance of the AdBreak class.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp associated with the command.
     * @param node      The JSON node associated with the command.
     * @param price     The price associated with the ad break.
     * @return The singleton instance of the AdBreak class.
     */
    public static AdBreak getInstance(final String command, final String username,
                                      final Integer timestamp, final ObjectNode node,
                                      final int price) {
        if (instance == null) {
            instance = new AdBreak(command, username, timestamp, node, price);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
            instance.setPrice(price);
        }
        return instance;
    }

    /**
     * Executes the ad break command.
     *
     * @return The result of the command as an ObjectNode.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        return node;
    }

    /**
     * Gets the price associated with the ad break.
     *
     * @return The price associated with the ad break.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price associated with the ad break.
     *
     * @param price The new price to be associated with the ad break.
     */
    public void setPrice(final int price) {
        this.price = price;
    }
}
