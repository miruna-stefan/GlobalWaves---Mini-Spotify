package main.statisticsCommands;

import users.Artist;
import users.Host;
import users.NormalUser;

public interface VisitorWrapped {
    /**
     * Gets the wrapped result node for a normal user.
     *
     * @param normalUser The normal user for which the result node is obtained.
     */
    void getWrappedResultNode(NormalUser normalUser);

    /**
     * Gets the wrapped result node for an artist.
     *
     * @param artist The artist for which the result node is obtained.
     */
    void getWrappedResultNode(Artist artist);

    /**
     * Gets the wrapped result node for a host.
     *
     * @param host The host for which the result node is obtained.
     */
    void getWrappedResultNode(Host host);
}
