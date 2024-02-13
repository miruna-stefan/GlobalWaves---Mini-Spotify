package fileio.input.wrappedEntities;

import users.NormalUser;

public class WrappedFan extends GeneralEntityWrapped {
    private NormalUser normalUser;

    public WrappedFan(final NormalUser normalUser) {
        this.setListens(0);
        this.normalUser = normalUser;
    }

    /**
     * Gets the NormalUser associated with this wrapped fan.
     *
     * @return The NormalUser associated with this wrapped fan.
     */
    public NormalUser getNormalUser() {
        return normalUser;
    }

    /**
     * Sets the NormalUser associated with this wrapped fan.
     *
     * @param normalUser The new NormalUser to be associated with this wrapped fan.
     */
    public void setNormalUser(final NormalUser normalUser) {
        this.normalUser = normalUser;
    }
}
