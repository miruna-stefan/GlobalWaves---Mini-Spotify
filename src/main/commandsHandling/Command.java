package main.commandsHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Command {
    /**
     * Executes the command and returns the result as an ObjectNode.
     *
     * @return The result of the command as an ObjectNode.
     */
    ObjectNode execute();
}
