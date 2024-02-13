package fileio.input.Pagination;

import fileio.input.entitiesForHost.Announcement;
import fileio.input.audioEntities.EpisodeInput;
import fileio.input.audioEntities.PodcastInput;
import users.Host;

import java.util.ArrayList;

public class HostPage extends Page {
    private static final int TYPE_HOST = 6;
    private Host hostPageOwner;

    public HostPage(final Host hostPageOwner) {
        super(TYPE_HOST);
        this.hostPageOwner = hostPageOwner;
    }

    /**
     * Converts the HostPage object to its string representation.
     *
     * @return A formatted string representation of the HostPage.
     */
    @Override
    public String pageToString() {
        return "Podcasts:\n\t" + getPodcastsString(this.hostPageOwner) + "\n\nAnnouncements:\n\t"
                + getAnnouncementsString(this.hostPageOwner);
    }

    /**
     * Retrieves and formats the details of podcasts for a Host.
     *
     * @param host the Host for whom podcast details are retrieved.
     * @return an ArrayList of strings containing details about podcasts.
     */
    public ArrayList<String> getPodcastsString(final Host host) {
        ArrayList<String> podcastsString = new ArrayList<>();
        for (PodcastInput podcast : host.getPodcasts()) {
            String podcastDetails = podcast.getName() + ":\n\t"
                    + getEpisodesString(podcast) + "\n";
            podcastsString.add(podcastDetails);
        }
        return podcastsString;
    }

    /**
     * Retrieves and formats the details of episodes for a PodcastInput.
     *
     * @param podcast the PodcastInput for whom episode details are retrieved.
     * @return an ArrayList of strings containing details about episodes.
     */
    public ArrayList<String> getEpisodesString(final PodcastInput podcast) {
        ArrayList<String> episodesString = new ArrayList<>();
        for (EpisodeInput episode : podcast.getEpisodes()) {
            String episodeDetails = episode.getName() + " - " + episode.getDescription();
            episodesString.add(episodeDetails);
        }
        return episodesString;
    }

    /**
     * Retrieves and formats the details of announcements for a Host.
     *
     * @param host the Host for whom announcement details are retrieved.
     * @return an ArrayList of strings containing details about announcements.
     */
    public ArrayList<String> getAnnouncementsString(final Host host) {
        ArrayList<String> announcementsString = new ArrayList<>();
        for (Announcement announcement : host.getAnnouncements()) {
            String announcementDetails = announcement.getName() + ":\n\t"
                    + announcement.getDescription() + "\n";
            announcementsString.add(announcementDetails);
        }
        return announcementsString;
    }

    /**
     * Gets the Host who owns this page.
     *
     * @return The Host who owns this page.
     */
    public Host getHostPageOwner() {
        return hostPageOwner;
    }

    /**
     * Sets the Host who owns this page.
     *
     * @param hostPageOwner The new Host who owns this page.
     */
    public void setHostPageOwner(final Host hostPageOwner) {
        this.hostPageOwner = hostPageOwner;
    }
}
