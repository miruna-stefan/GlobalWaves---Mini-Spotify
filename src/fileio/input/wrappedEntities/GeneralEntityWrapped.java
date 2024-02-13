package fileio.input.wrappedEntities;

public class GeneralEntityWrapped {
    private int listens;

    public GeneralEntityWrapped() {
        listens = 1;
    }

    /**
     * Gets the number of listens associated with this wrapped entity.
     *
     * @return The number of listens associated with this wrapped entity.
     */
    public int getListens() {
        return listens;
    }

    /**
     * Sets the number of listens associated with this wrapped entity.
     *
     * @param listens The new number of listens to be associated with this wrapped entity.
     */
    public void setListens(final int listens) {
        this.listens = listens;
    }
}
