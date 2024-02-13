package main.adminCommands;

public interface VisitableDeletion {
    /**
     * Accepts a deletion visitor to perform deletion-related operations.
     *
     * @param visitor The deletion visitor to accept.
     * @return True if the object can be deleted, false otherwise.
     */
    Boolean acceptDeletion(VisitorDeletion visitor);
}
