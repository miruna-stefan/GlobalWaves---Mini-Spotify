package main.artistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForArtist.Merch;
import main.Main;
import users.Artist;

public final class AddMerch extends StandardArtistCommand {
    private String description;
    private Integer price;

    // Singleton instance field
    private static AddMerch instance = null;

    private AddMerch(final String command, final String username, final Integer timestamp,
                     final String name, final String description, final Integer price,
                     final ObjectNode node) {
        super(command, username, timestamp, name, node);
        this.description = description;
        this.price = price;
    }

    /**
     * Gets the singleton instance of AddMerch command with the specified parameters.
     * If the instance is null, creates a new instance; otherwise, updates the existing
     * instance with new parameters.
     *
     * @param command     the command string.
     * @param username    the username associated with the command.
     * @param timestamp   the timestamp of the command.
     * @param name        the name associated with the command.
     * @param description the description of the merchandise.
     * @param price       the price of the merchandise.
     * @param node        the JSON node associated with the command.
     * @return the singleton instance of AddMerch command.
     */
    public static AddMerch getInstance(final String command, final String username,
                                       final Integer timestamp, final String name,
                                       final String description, final Integer price,
                                       final ObjectNode node) {
        if (instance == null) {
            instance = new AddMerch(command, username, timestamp, name,
                    description, price, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
            instance.setDescription(description);
            instance.setPrice(price);
        }
        return instance;
    }

    /**
     * Prints the result of the command, which includes the user, timestamp,
     * and the updated volume of the user.
     *
     * @return The ObjectNode containing information about the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getCommandMessage());
        return node;
    }

    /**
     * Executes the AddMerch command, adding new merchandise to the artist's list.
     *
     * @return a message indicating the success or failure of the command.
     */
    public String getCommandMessage() {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // this artist has already been created => the command can be executed

                // check if the event name is already taken
                for (Merch merch : artist.getMerchItems()) {
                    if (merch.getName().equals(this.getName())) {
                        return this.getUsername() + " has merchandise with the same name.";
                    }
                }

                // check price validity
                if (this.price < 0) {
                    return "Price for merchandise can not be negative.";
                }

                /* if we reached this point, all the validity checks were
                passed and we need to add the merch */
                Merch newMerch = new Merch(this.getName(), this.description, this.price);
                artist.getMerchItems().add(newMerch);

                // prepare the notification message
                ObjectNode newNotification = JsonNodeFactory.instance.objectNode();
                newNotification.put("name", "New Merchandise");
                newNotification.put("description", "New Merchandise from "
                        + artist.getUsername() + ".");
                artist.notifyObservers(newNotification);

                return this.getUsername() + " has added new merchandise successfully.";
            }
        }

        return checkUsernameExistence();
    }

    /**
     * Gets the description of the merchandise.
     *
     * @return the description of the merchandise.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the merchandise.
     *
     * @param description the description to set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the price of the merchandise.
     *
     * @return the price of the merchandise.
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Sets the price of the merchandise.
     *
     * @param price the price to set.
     */
    public void setPrice(final Integer price) {
        this.price = price;
    }
}
