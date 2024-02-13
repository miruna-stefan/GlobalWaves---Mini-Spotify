package main.statisticsCommands;

public interface VisitableWrapped {
    /**
     * Accepts a VisitorWrapped for performing operations on the wrapped element.
     *
     * @param visitor The VisitorWrapped to accept.
     */
    void acceptWrapped(VisitorWrapped visitor);
}
