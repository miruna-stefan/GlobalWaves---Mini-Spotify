package fileio.input.entitiesForHost;

public class Announcement {
    private String name;
    private String description;

    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of the announcement.
     *
     * @return The name of the announcement.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the announcement.
     *
     * @param name The new name for the announcement.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the description of the announcement.
     *
     * @return The description of the announcement.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the announcement.
     *
     * @param description The new description for the announcement.
     */
    public void setDescription(final String description) {
        this.description = description;
    }
}
