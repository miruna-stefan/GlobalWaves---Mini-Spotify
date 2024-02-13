package fileio.input.entitiesForArtist;

public class Merch {
    private String name;
    private String description;
    private Integer price;

    public Merch(final String name, final String description, final Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    /**
     * Gets the name of the merchandise.
     *
     * @return The name of the merchandise.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the merchandise.
     *
     * @param name The new name of the merchandise.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the description of the merchandise.
     *
     * @return The description of the merchandise.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the merchandise.
     *
     * @param description The new description of the merchandise.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the price of the merchandise.
     *
     * @return The price of the merchandise.
     */
    public Integer getPrice() {
        return price;
    }

    /**
     * Sets the price of the merchandise.
     *
     * @param price The new price of the merchandise.
     */
    public void setPrice(final Integer price) {
        this.price = price;
    }
}
