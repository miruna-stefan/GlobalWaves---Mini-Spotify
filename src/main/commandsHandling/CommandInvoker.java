package main.commandsHandling;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.InputCommands;
import fileio.input.LibraryInput;
import fileio.input.audioEntities.Playlist;

import java.util.ArrayList;

public class CommandInvoker {
    private ArrayList<InputCommands> inputcommands;
    private ArrayNode outputs;
    private LibraryInput library;
    private ArrayList<Playlist> allUsersPlaylists;

    public CommandInvoker(final ArrayList<InputCommands> inputcommands,
                          final ArrayNode outputs, final LibraryInput library,
                          final ArrayList<Playlist> allUsersPlaylists) {
        this.inputcommands = inputcommands;
        this.outputs = outputs;
        this.library = library;
        this.allUsersPlaylists = allUsersPlaylists;
    }

    /**
     * Invokes all the commands in the inputcommands list and adds their
     * outputs to the outputs ArrayNode.
     */
    public void invokeCommands() {
        for (InputCommands inputcommand : inputcommands) {
            ObjectNode node = JsonNodeFactory.instance.objectNode();

            GeneralCommand command = CommandFactory.createCommand(inputcommand,
                    library, node, allUsersPlaylists);
            outputs.addPOJO(CommandFactory.createCommand(inputcommand, library,
                    node, allUsersPlaylists).execute());
        }
    }

    /**
     * Gets the list of input commands.
     *
     * @return The list of input commands.
     */
    public ArrayList<InputCommands> getInputcommands() {
        return inputcommands;
    }

    /**
     * Sets the list of input commands.
     *
     * @param inputcommands The new list of input commands.
     */
    public void setInputcommands(final ArrayList<InputCommands> inputcommands) {
        this.inputcommands = inputcommands;
    }

    /**
     * Gets the library containing the initial state.
     *
     * @return The LibraryInput containing the initial state.
     */
    public LibraryInput getLibrary() {
        return library;
    }

    /**
     * Sets the library containing the initial state.
     *
     * @param library The new LibraryInput containing the initial state.
     */
    public void setLibrary(final LibraryInput library) {
        this.library = library;
    }

    /**
     * Gets the list of all playlists from all users.
     *
     * @return The list of all playlists.
     */
    public ArrayList<Playlist> getAllUsersPlaylists() {
        return allUsersPlaylists;
    }

    /**
     * Sets the list of all playlists from all users.
     *
     * @param allUsersPlaylists The new list of all playlists.
     */
    public void setAllUsersPlaylists(final ArrayList<Playlist> allUsersPlaylists) {
        this.allUsersPlaylists = allUsersPlaylists;
    }

    /**
     * Gets the ArrayNode containing the outputs of the invoked commands.
     *
     * @return The ArrayNode containing the outputs.
     */
    public ArrayNode getOutputs() {
        return outputs;
    }

    /**
     * Sets the ArrayNode containing the outputs of the invoked commands.
     *
     * @param outputs The new ArrayNode containing the outputs.
     */
    public void setOutputs(final ArrayNode outputs) {
        this.outputs = outputs;
    }
}
