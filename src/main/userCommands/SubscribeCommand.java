package main.userCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.Pagination.ArtistPage;
import fileio.input.Pagination.HostPage;
import main.Main;
import users.NormalUser;

public final class SubscribeCommand extends StandardCommandForUserPlayer {
    private static final int TYPE_ARTIST = 5;
    private static final int TYPE_HOST = 6;

    // Singleton instance field
    private static SubscribeCommand instance = null;

    private SubscribeCommand(final String command, final String username,
                             final Integer timestamp, final ObjectNode node) {
        super(command, timestamp, node, username);
    }

    /**
     * Gets the singleton instance of SubscribeCommand.
     *
     * @param command   The command string.
     * @param username  The username associated with the command.
     * @param timestamp The timestamp of the command.
     * @param node      The JSON node associated with the command.
     * @return The singleton instance of SubscribeCommand.
     */
    public static SubscribeCommand getInstance(final String command, final String username,
                                               final Integer timestamp,
                                               final ObjectNode node) {
        if (instance == null) {
            instance = new SubscribeCommand(command, username, timestamp, node);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Retrieves the subscription message based on the user's page type.
     *
     * @return A String message indicating the result of the subscription.
     */
    public String getSubscribeMessage() {
        // check if the username exists in the database
        NormalUser user = null;
        for (NormalUser normalUser : Main.normalUserList) {
            if (normalUser.getUsername().equals(this.getUsername())) {
                user = normalUser;
            }
        }
        if (user == null) {
            return "The username " + this.getUsername() + " doesn't exist.";
        }

        // check if the user is on an artist's or on a host's page
        if (user.getPageHistory().get(user.getCurrentPageIndex()).getPageType() == TYPE_ARTIST) {
            return subscribeToArtist(user);
        }

        if (user.getPageHistory().get(user.getCurrentPageIndex()).getPageType() == TYPE_HOST) {
            return subscribeToHost(user);
        }

        return "To subscribe you need to be on the page of an artist or host.";
    }

    /**
     * Subscribes or unsubscribes the user to/from an artist based on the current status.
     *
     * @param user The user subscribing or unsubscribing.
     * @return A String message indicating the result of the subscription.
     */
    public String subscribeToArtist(final NormalUser user) {
        ArtistPage artistPage = (ArtistPage) user.getPageHistory().get(user.getCurrentPageIndex());

        // check if the current user already is a subscriber of the artist
        for (NormalUser subscriber : artistPage.getArtistPageOwner().getSubscribers()) {
            if (subscriber.getUsername().equals(user.getUsername())) {
                // the user is already a subscriber => we need to unsubscribe him
                artistPage.getArtistPageOwner().removeObserver(user);
                return this.getUsername() + " unsubscribed from "
                        + artistPage.getArtistPageOwner().getUsername()
                        + " successfully.";
            }
        }

        // if we reached this point, the user is not a subscriber => we need to subscribe him
        artistPage.getArtistPageOwner().addObserver(user);
        return this.getUsername() + " subscribed to "
                + artistPage.getArtistPageOwner().getUsername() + " successfully.";
    }

    /**
     * Subscribes or unsubscribes the user to/from a host based on the current status.
     *
     * @param user The user subscribing or unsubscribing.
     * @return A String message indicating the result of the subscription.
     */
    public String subscribeToHost(final NormalUser user) {
        HostPage hostPage = (HostPage) user.getPageHistory().get(user.getCurrentPageIndex());

        // check if the current user already is a subscriber of the host
        for (NormalUser subscriber : hostPage.getHostPageOwner().getSubscribers()) {
            if (subscriber.getUsername().equals(user.getUsername())) {
                // the user is already a subscriber => we need to unsubscribe him
                hostPage.getHostPageOwner().removeObserver(user);
                return this.getUsername() + " unsubscribed from "
                        + hostPage.getHostPageOwner().getUsername() + " successfully.";
            }
        }

        // if we reached this point, the user is not a subscriber => we need to subscribe him
        hostPage.getHostPageOwner().addObserver(user);
        return this.getUsername() + " subscribed to "
                + hostPage.getHostPageOwner().getUsername() + " successfully.";
    }

    /**
     * Executes the SubscribeCommand.
     *
     * @return The JSON node containing the results of the command execution.
     */
    public ObjectNode execute() {
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        node.put("message", this.getSubscribeMessage());
        return node;
    }

}
