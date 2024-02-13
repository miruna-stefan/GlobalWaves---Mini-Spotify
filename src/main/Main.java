package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

import fileio.input.audioEntities.PodcastInput;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongInput;
import fileio.input.audioEntities.SongPlayInfo;
import fileio.input.LibraryInput;
import fileio.input.InputCommands;
import fileio.input.UserInput;
import main.commandsHandling.CommandInvoker;
import users.Artist;
import users.Host;
import users.NormalUser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ArrayList;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";
    public static ArrayList<SongPlayInfo> songsList;
    public static ArrayList<PodcastInput> podcastsList;

    public static ArrayList<NormalUser> normalUserList;
    public static ArrayList<Artist> artistsList;
    public static ArrayList<Host> hostsList;

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput  for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();

        // TODO add your implementation
        ArrayList<InputCommands> inputcommands;
        inputcommands = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                + filePathInput), new TypeReference<ArrayList<InputCommands>>() {
        });

        // instantiate endProgram command
        InputCommands endProgram = new InputCommands();
        endProgram.setCommand("endProgram");
        Integer lastTimestamp = inputcommands.get(inputcommands.size() - 1).getTimestamp();
        endProgram.setTimestamp(lastTimestamp);

        // add endProgram command at the end of the commands list
        inputcommands.add(endProgram);

        songsList = new ArrayList<>();
        for (SongInput song : library.getSongs()) {
            SongPlayInfo newSong = new SongPlayInfo();
            newSong.setSong(song);
            songsList.add(newSong);
        }

        podcastsList = new ArrayList<>();
        podcastsList.addAll(library.getPodcasts());

        normalUserList = new ArrayList<>();
        artistsList = new ArrayList<>();
        hostsList = new ArrayList<>();
        ArrayList<Playlist> allUsersPlaylists = new ArrayList<>();

        for (UserInput userInput : library.getUsers()) {
            NormalUser newUser = new NormalUser(userInput.getUsername());
            normalUserList.add(newUser);
        }

        CommandInvoker commandInvoker = new CommandInvoker(inputcommands,
                outputs, library, allUsersPlaylists);
        commandInvoker.invokeCommands();

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
