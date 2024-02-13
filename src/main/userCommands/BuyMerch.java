package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.ArtistPage;
import fileio.input.entitiesForArtist.Merch;
import main.Main;
import users.NormalUser;

public final class BuyMerch extends StandardCommandForUserPlayer {
    private static final int TYPE_ARTIST = 5;
    private String name;

    private static BuyMerch instance = null;

    private BuyMerch(final String command, final String username,
                    final Integer timestamp, final ObjectNode node, final String name) {
        super(command, timestamp, node, username);
        this.name = name;
    }

    /**
     * Gets an instance of the BuyMerch class.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp associated with the command.
     * @param node      The JSON node associated with the command.
     * @param name      The name of the merch to be bought.
     * @return An instance of the BuyMerch class.
     */
    public static BuyMerch getInstance(final String command, final String username,
                    final Integer timestamp, final ObjectNode node, final String name) {
        if (instance == null) {
            instance = new BuyMerch(command, username, timestamp, node, name);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
            instance.setName(name);
        }
        return instance;
    }

    /**
     * Gets the command message indicating the result of attempting to buy merch.
     *
     * @return The command message.
     */
    public String getCommandMessage() {
        NormalUser user = null;
        // check if the username exists
        for (NormalUser normalUser : Main.normalUserList) {
            if (normalUser.getUsername().equals(this.getUsername())) {
                user = normalUser;
            }
        }
        if (user == null) {
            return "The username " + this.getUsername() + " doesn't exist.";
        }

        // check if the user is on an artist's page
        if (user.getPageHistory().get(user.getCurrentPageIndex()).getPageType() != TYPE_ARTIST) {
            return "Cannot buy merch from this page.";
        }


        // check if the merch exists
        ArtistPage artistPage = (ArtistPage) user.getPageHistory().get(user.getCurrentPageIndex());
        for (Merch artistMerch : artistPage.getArtistPageOwner().getMerchItems()) {
            if (artistMerch.getName().equals(this.name)) {
                user.getBoughtMerch().add(artistMerch);
                artistPage.getArtistPageOwner().setMerchRevenue(artistPage.getArtistPageOwner().
                        getMerchRevenue() + artistMerch.getPrice());
                return this.getUsername() + " has added new merch successfully.";
            }
        }

        return "The merch " + this.getName() + " doesn't exist.";
    }

    /**
     * Executes the BuyMerch command.
     *
     * @return The JSON node containing the result of the execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getCommandMessage());
        return node;
    }

    /**
     * Gets the name of the merch to be bought.
     *
     * @return The name of the merch.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the merch to be bought.
     *
     * @param name The new name of the merch.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
