package users;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.entitiesForHost.Announcement;
import fileio.input.audioEntities.PodcastInput;
import main.Main;
import main.adminCommands.VisitableDeletion;
import main.adminCommands.VisitorDeletion;
import main.statisticsCommands.VisitableWrapped;
import main.statisticsCommands.VisitorWrapped;

import java.util.ArrayList;

public class Host extends GeneralUser implements VisitableDeletion, VisitableWrapped, Subject {
    private static final int TYPE_PODCAST = 2;
    private ArrayList<PodcastInput> podcasts;
    private ArrayList<Announcement> announcements;

    // list of all the distinct users that have listened to any the host's podcasts
    private ArrayList<NormalUser> listeners;
    private ArrayList<NormalUser> subscribers;

    public Host(final String type, final String username, final Integer age,
                final String city) {
        super(type, username, age, city);

        this.podcasts = new ArrayList<>();
        for (PodcastInput podcast : Main.podcastsList) {
            if (podcast.getOwner().equals(username)) {
                this.podcasts.add(podcast);
            }
        }

        this.announcements = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }

    /**
     * Adds an observer (subscriber) to the host's subscribers list.
     *
     * @param observer The observer to be added.
     */
    public void addObserver(final NormalUser observer) {
        subscribers.add(observer);
    }

    /**
     * Removes an observer (subscriber) from the host's subscribers list.
     *
     * @param observer The observer to be removed.
     */
    public void removeObserver(final NormalUser observer) {
        subscribers.remove(observer);
    }

    /**
     * Notifies all subscribers with a new notification.
     *
     * @param newNotification The new notification to be sent.
     */
    public void notifyObservers(final ObjectNode newNotification) {
        for (NormalUser observer : subscribers) {
            observer.updateNotifications(newNotification);
        }
    }

    /**
     * Checks if the specified podcast can be deleted by the host.
     * This method considers whether any user currently has the podcast loaded in the player.
     *
     * @param podcastToBeDeleted The podcast to be deleted.
     * @param timestamp          The timestamp used for updating podcast status.
     * @return True if the podcast can be deleted; false otherwise.
     */
    public Boolean canDeletePodcast(final PodcastInput podcastToBeDeleted,
                                    final Integer timestamp) {
        // check if any user has the given podcast loaded in the player
        for (NormalUser user : Main.normalUserList) {
            // check if the user has anything loaded in the player
            if (!user.getLoaded()) {
                continue;
            }

            // check if the user has loaded a podcast
            if (user.getLastLoadTypeIndicator() != TYPE_PODCAST) {
                continue;
            }

            // update status of the loaded podcast in the player
            if (!user.getLastLoadedPodcast().getPodcastPaused()) {
                user.getLastLoadedPodcast().updatePodcastStatus(user, timestamp);
                // check if the podcast has ended meanwhile
                if (user.getLastLoadedPodcast() == null) {
                    continue;
                }
                user.getLastLoadedPodcast().setLastPlayTimestamp(timestamp);
                user.getLastLoadedPodcast().setPodcastPaused(false);
            }

            // check if the podcast to be deleted is the one loaded in the player
            if (user.getLastLoadedPodcast().getPodcast().getName()
                    .equals(podcastToBeDeleted.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prepares the removal of the specified podcast by removing it from the main podcast list
     * and updating the user selections if necessary.
     *
     * @param podcastToBeDeleted The podcast to be removed.
     */
    public void preparePodcastRemoval(final PodcastInput podcastToBeDeleted) {
        // remove podcast from the big list with all the podcasts in the app
        for (PodcastInput podcast : Main.podcastsList) {
            if (podcast.getName().equals(podcastToBeDeleted.getName())) {
                Main.podcastsList.remove(podcast);
                break;
            }
        }

        // check if any user has this podcast selected
        for (NormalUser user : Main.normalUserList) {
            if (!user.getSearched() || !user.getSelected()) {
                continue;
            }
            if (user.getLastSearchTypeIndicator() == TYPE_PODCAST) {
                if (user.getLastSelectedPodcast().getName().equals(podcastToBeDeleted.getName())) {
                    user.setSelected(false);
                    user.setLastSelectedPodcast(null);
                }
            }
        }
    }

    /**
     * Accepts a deletion visitor to determine if the host can be deleted.
     *
     * @param visitor The deletion visitor.
     * @return True if the host can be deleted; false otherwise.
     */
    @Override
    public Boolean acceptDeletion(final VisitorDeletion visitor) {
        return visitor.canBeDeleted(this);
    }

    /**
     * Accepts a wrapped visitor to get the wrapped result node.
     *
     * @param visitor The wrapped visitor.
     */
    @Override
    public void acceptWrapped(final VisitorWrapped visitor) {
        visitor.getWrappedResultNode(this);
    }


    /**
     * Gets the list of podcasts managed by the host.
     *
     * @return The list of podcasts.
     */
    public ArrayList<PodcastInput> getPodcasts() {
        return podcasts;
    }

    /**
     * Sets the list of podcasts managed by the host.
     *
     * @param podcasts The new list of podcasts.
     */
    public void setPodcasts(final ArrayList<PodcastInput> podcasts) {
        this.podcasts = podcasts;
    }

    /**
     * Gets the list of announcements made by the host.
     *
     * @return The list of announcements.
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * Sets the list of announcements made by the host.
     *
     * @param announcements The new list of announcements.
     */
    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Gets the list of listeners who have listened to the host's podcasts.
     *
     * @return The list of listeners.
     */
    public ArrayList<NormalUser> getListeners() {
        return listeners;
    }

    /**
     * Sets the list of listeners who have listened to the host's podcasts.
     *
     * @param listeners The new list of listeners.
     */
    public void setListeners(final ArrayList<NormalUser> listeners) {
        this.listeners = listeners;
    }

    /**
     * Gets the list of subscribers to the host's podcasts.
     *
     * @return The list of subscribers.
     */
    public ArrayList<NormalUser> getSubscribers() {
        return subscribers;
    }

    /**
     * Sets the list of subscribers to the host's podcasts.
     *
     * @param subscribers The new list of subscribers.
     */
    public void setSubscribers(final ArrayList<NormalUser> subscribers) {
        this.subscribers = subscribers;
    }
}
