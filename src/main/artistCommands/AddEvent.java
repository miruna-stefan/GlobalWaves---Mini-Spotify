package main.artistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForArtist.Event;
import main.Main;
import users.Artist;

public final class AddEvent extends StandardArtistCommand {
    private String description;
    private String date;

    private static final int DATE_FORMAT_LENGTH = 10;
    private static final int SEPARATION_BETWEEN_MONTH_AND_YEAR = 5;
    private static final int SEPARATION_BETWEEN_DAY_AND_MONTH = 2;
    private static final int MAX_MONTH = 12;
    private static final int MAX_DAY_NORMAL_MONTH = 31;
    private static final int MAX_DAY_FEBRUARY = 28;
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2023;


    // Singleton instance field
    private static AddEvent instance = null;

    private AddEvent(final String command, final String username, final Integer timestamp,
                     final String name, final ObjectNode node, final String description,
                     final String date) {
        super(command, username, timestamp, name, node);
        this.description = description;
        this.date = date;
    }

    /**
     * Gets the singleton instance of AddEvent.
     *
     * @param command      the command string.
     * @param username     the username associated with the command.
     * @param timestamp    the timestamp of the command.
     * @param name         the name of the event to be added.
     * @param node         the JSON node associated with the command.
     * @param description  the description of the event.
     * @param date         the date of the event.
     * @return the singleton instance of AddEvent.
     */
    public static AddEvent getInstance(final String command, final String username,
                                       final Integer timestamp, final String name,
                                       final ObjectNode node, final String description,
                                       final String date) {
        if (instance == null) {
            instance = new AddEvent(command, username, timestamp, name, node, description, date);
        } else {
            instance.setCommand(command);
            instance.setUsername(username);
            instance.setTimestamp(timestamp);
            instance.setName(name);
            instance.setNode(node);
            instance.setDescription(description);
            instance.setDate(date);
        }
        return instance;
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
        node.put("message", this.getCommandMessage());
        return node;
    }

    /**
     * Checks if the date format is valid and represents a valid date.
     *
     * @return true if the date format is valid and represents a valid date, false otherwise.
     */
    public Boolean isValidDate() {
        // check if the length of the string is valid
        if (this.date.length() != DATE_FORMAT_LENGTH) {
            return false;
        }

        // check if the '-' characters are placed correctly
        if (this.date.charAt(SEPARATION_BETWEEN_DAY_AND_MONTH) != '-'
                || this.date.charAt(SEPARATION_BETWEEN_MONTH_AND_YEAR) != '-') {
            return false;
        }

        // check if all the characters that are not '-' are digits
        for (int i = 0; i < this.date.length(); i++) {
            if (i != SEPARATION_BETWEEN_DAY_AND_MONTH
                    && i != SEPARATION_BETWEEN_MONTH_AND_YEAR) {
                if (!Character.isDigit(this.date.charAt(i))) {
                    return false;
                }
            }
        }

        // if we reached this point, it means that the String format is valid
        // now we need to check if the value of the date day / month / year is valid
        Integer month = Integer.parseInt(this.date
                .substring(SEPARATION_BETWEEN_DAY_AND_MONTH + 1,
                        SEPARATION_BETWEEN_MONTH_AND_YEAR));
        if (month < 1 || month > MAX_MONTH) {
            return false;
        }

        Integer day = Integer.parseInt(this.date.substring(0, 2));
        // check if the month is february or a regular month
        if (month == 2) {
            if (day < 1 || day > MAX_DAY_FEBRUARY) {
                return false;
            }
        } else {
            if (day < 1 || day > MAX_DAY_NORMAL_MONTH) {
                return false;
            }
        }

        Integer year = Integer.parseInt(this.date
                .substring(SEPARATION_BETWEEN_MONTH_AND_YEAR + 1,
                        DATE_FORMAT_LENGTH));
        if (year < MIN_YEAR || year > MAX_YEAR) {
            return false;
        }

        return true;
    }


    /**
     * Gets the command message indicating the result of adding an event.
     *
     * @return The command message.
     */
    public String getCommandMessage() {
        for (Artist artist : Main.artistsList) {
            if (artist.getUsername().equals(this.getUsername())) {
                // this artist has already been created => the command can be executed

                // check if the event name is already taken
                for (Event event : artist.getEvents()) {
                    if (event.getName().equals(this.getName())) {
                        return this.getUsername() + " has another event with the same name.";
                    }
                }

                // check date validity
                if (!isValidDate()) {
                    return "Event for " + this.getUsername() + " does not have a valid date.";
                }

                /* if we reached this point, all the validity checks were
                passed and we need to add the event */
                Event newEvent = new Event(this.getName(), this.description, this.date);
                artist.getEvents().add(newEvent);

                // prepare the notification message
                ObjectNode newNotification = JsonNodeFactory.instance.objectNode();
                newNotification.put("name", "New Event");
                newNotification.put("description", "New Event from " + artist.getUsername() + ".");
                artist.notifyObservers(newNotification);

                return this.getUsername() + " has added new event successfully.";
            }
        }

        return checkUsernameExistence();
    }



    /**
     * Gets the description of the event.
     *
     * @return the description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the description to be set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the date of the event.
     *
     * @return the date of the event.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the event.
     *
     * @param date the date to be set.
     */
    public void setDate(final String date) {
        this.date = date;
    }
}
