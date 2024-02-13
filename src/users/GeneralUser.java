package users;

public class GeneralUser {
    private String type;
    private String username;
    private Integer age;
    private String city;

    public GeneralUser(final String type, final String username, final Integer age,
                       final String city) {
        this.type = type;
        this.username = username;
        this.age = age;
        this.city = city;
    }

    public GeneralUser(final String type, final String username) {
        this.type = type;
        this.username = username;
    }

    /**
     * Gets the type of the user.
     *
     * @return the type of the user.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the user.
     *
     * @param type the type to be set for the user.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the username associated with the user.
     *
     * @return the username associated with the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with the user.
     *
     * @param username the username to be set for the user.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the age of the user.
     *
     * @return the age of the user.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age of the user.
     *
     * @param age the age to be set for the user.
     */
    public void setAge(final Integer age) {
        this.age = age;
    }

    /**
     * Gets the city where the user is located.
     *
     * @return the city where the user is located.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the user is located.
     *
     * @param city the city to be set for the user.
     */
    public void setCity(final String city) {
        this.city = city;
    }
}
