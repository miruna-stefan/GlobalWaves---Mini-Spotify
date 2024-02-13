package users;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Subject {
    /**
     * Adds a NormalUser observer to the list of subscribers.
     *
     * @param observer The observer to be added.
     */
    void addObserver(NormalUser observer);

    /**
     * Removes a NormalUser observer from the list of subscribers.
     *
     * @param observer The observer to be removed.
     */
    void removeObserver(NormalUser observer);

    /**
     * Notifies all subscribed observers with a new notification.
     *
     * @param newNotification The new notification to be sent to the observers.
     */
    void notifyObservers(ObjectNode newNotification);
}
