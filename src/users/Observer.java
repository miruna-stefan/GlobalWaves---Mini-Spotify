package users;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Observer {
    /**
     * Updates the observer with a new notification.
     *
     * @param newNotification The new notification received by the observer.
     */
    void updateNotifications(ObjectNode newNotification);
}
