package main.adminCommands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Main;
import users.Artist;
import users.Host;
import users.NormalUser;

public final class AddUser extends StandardAdminCommand {
    private String type;
    private Integer age;
    private String city;

    // Singleton instance field
    private static AddUser instance = null;


    private AddUser(final String command, final Integer timestamp,
                    final String type, final String username,
                   final Integer age, final String city,
                    final ObjectNode node) {
        super(command, username, timestamp, node);
        this.type = type;
        this.age = age;
        this.city = city;
    }

    /**
     * Gets the singleton instance of `AddUser` with the specified parameters.
     *
     * @param command   the command associated with the command.
     * @param timestamp the timestamp of the command.
     * @param type      the type of user to be added (user, artist, or host).
     * @param username  the username of the user to be added.
     * @param age       the age of the user to be added (for artists and hosts).
     * @param city      the city of the user to be added (for artists and hosts).
     * @param node      the JSON node associated with the command.
     * @return the singleton instance of `AddUser`.
     */
    public static AddUser getInstance(final String command, final Integer timestamp,
                                      final String type, final String username,
                                      final Integer age, final String city,
                                      final ObjectNode node) {
        if (instance == null) {
            instance = new AddUser(command, timestamp, type, username, age, city, node);
        } else {
            instance.setCommand(command);
            instance.setTimestamp(timestamp);
            instance.setType(type);
            instance.setUsername(username);
            instance.setAge(age);
            instance.setCity(city);
            instance.setNode(node);
        }
        return instance;
    }

    /**
     * Executes the `AddUser` command, adding a new user, artist, or host to the system.
     *
     * @return a message indicating the success or failure of the user addition.
     */
    public String addUserAccordingToItsType() {
        // look for the current username in each list
        for (NormalUser user : Main.normalUserList) {
            if (user.getUsername().equals(this.getUsername())) {
                return "The username " + this.getUsername() + " is already taken.";
            }
        }

        for (Artist artist: Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                return "The username " + this.getUsername() + " is already taken.";
            }
        }

        for (Host host: Main.hostsList) {
            if (host.getUsername().equals(this.getUsername())) {
                return "The username " + this.getUsername() + " is already taken.";
            }
        }

        // if the username is not taken, add the user to the correspondent list
        if (type.equals("user")) {
            NormalUser newUser = new NormalUser(getUsername());
            Main.normalUserList.add(newUser);
            return "The username " + this.getUsername() + " has been added successfully.";
        }

        if (type.equals("artist")) {
            Artist newArtist = new Artist(type, getUsername(), age, city);
            Main.artistsList.add(newArtist);
            return "The username " + this.getUsername() + " has been added successfully.";
        }

        if (type.equals("host")) {
            Host newHost = new Host(type, getUsername(), age, city);
            Main.hostsList.add(newHost);
            return "The username " + this.getUsername() + " has been added successfully.";
        }

        return null;
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
        node.put("message", this.addUserAccordingToItsType());
        return node;
    }

    /**
     * Gets the type of the user to be added.
     *
     * @return the type of the user (user, artist, or host).
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the user to be added.
     *
     * @param type the type of the user (user, artist, or host).
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the age of the user to be added.
     *
     * @return the age of the user (for artists and hosts).
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age of the user to be added.
     *
     * @param age the age of the user (for artists and hosts).
     */
    public void setAge(final Integer age) {
        this.age = age;
    }

    /**
     * Gets the city of the user to be added.
     *
     * @return the city of the user (for artists and hosts).
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the user to be added.
     *
     * @param city the city of the user (for artists and hosts).
     */
    public void setCity(final String city) {
        this.city = city;
    }
}
