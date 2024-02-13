package main.adminCommands;

import users.Artist;
import users.Host;
import users.NormalUser;

public interface VisitorDeletion {
    /**
     * Checks if a normal user can be deleted.
     *
     * @param normalUser The normal user to be deleted.
     * @return True if the user can be deleted, false otherwise.
     */

    Boolean canBeDeleted(NormalUser normalUser);
    /**
     * Checks if an artist can be deleted.
     *
     * @param artist The artist to be deleted.
     * @return True if the artist can be deleted, false otherwise.
     */
    Boolean canBeDeleted(Artist artist);

    /**
     * Checks if a host can be deleted.
     *
     * @param host The host to be deleted.
     * @return True if the host can be deleted, false otherwise.
     */
    Boolean canBeDeleted(Host host);
}
