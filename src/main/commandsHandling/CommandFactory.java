package main.commandsHandling;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.InputCommands;
import fileio.input.audioEntities.Playlist;
import fileio.input.audioEntities.SongInput;
import fileio.input.audioEntities.SongPlayInfo;
import main.EndProgram;
import main.Main;
import main.adminCommands.AddUser;
import main.adminCommands.DeleteUser;
import main.adminCommands.ShowAlbums;
import main.adminCommands.ShowPodcasts;
import main.artistCommands.AddAlbum;
import main.artistCommands.AddEvent;
import main.artistCommands.AddMerch;
import main.artistCommands.RemoveAlbum;
import main.artistCommands.RemoveEvent;
import main.hostCommands.AddAnnouncement;
import main.hostCommands.AddPodcast;
import main.hostCommands.RemoveAnnouncement;
import main.hostCommands.RemovePodcast;
import main.statisticsCommands.GetOnlineUsers;
import main.statisticsCommands.GetAllUsers;
import main.statisticsCommands.GetTop5SongsCommand;
import main.statisticsCommands.GetTop5Playlists;
import main.statisticsCommands.GetTop5Albums;
import main.statisticsCommands.GetTop5Artists;
import main.statisticsCommands.WrappedCommand;
import main.statisticsCommands.UpdateRecommendations;
import main.userCommands.SubscribeCommand;
import main.userCommands.GetNotifications;
import main.userCommands.BuyPremium;
import main.userCommands.CancelPremium;
import main.userCommands.BuyMerch;
import main.userCommands.SeeMerch;
import main.userCommands.playerCommands.LoadRecommendations;
import main.userCommands.PageNavigationCommands.PreviousPage;
import main.userCommands.PageNavigationCommands.NextPage;
import main.userCommands.playerCommands.LoadCommand;
import main.userCommands.playerCommands.PlayPauseCommand;
import main.userCommands.playerCommands.StatusCommand;
import main.userCommands.userPlaylistHandling.CreatePlaylist;
import main.userCommands.userPlaylistHandling.AddRemoveInPlaylist;
import main.userCommands.userPlaylistHandling.ShowPlaylists;
import main.userCommands.playerCommands.LikeCommand;
import main.userCommands.ShowPreferredSongs;
import main.userCommands.playerCommands.RepeatCommand;
import main.userCommands.playerCommands.ShuffleCommand;
import main.userCommands.userPlaylistHandling.SwitchVisibilityCommand;
import main.userCommands.userPlaylistHandling.FollowPlaylistCommand;
import main.userCommands.playerCommands.PrevCommand;
import main.userCommands.playerCommands.NextCommand;
import main.userCommands.playerCommands.ForwardCommand;
import main.userCommands.playerCommands.BackwardCommand;
import main.userCommands.SwitchConnectionStatus;
import main.userCommands.PageNavigationCommands.ChangePage;
import main.userCommands.SearchCommand;
import main.userCommands.SelectCommand;
import main.userCommands.PageNavigationCommands.PrintCurrentPage;
import main.userCommands.AdBreak;

import java.util.ArrayList;

final class CommandFactory {
    private CommandFactory() {

    }
    public static GeneralCommand createCommand(final InputCommands inputcommand,
                                               final LibraryInput library, final ObjectNode node,
                                               final ArrayList<Playlist> allUsersPlaylists) {
        switch (inputcommand.getCommand()) {
            case "search":
                return SearchCommand.getInstance(library, inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getType(), inputcommand.getFilters(), node);
            case "select":
                return SelectCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getItemNumber(), node);
            case "load":
                return LoadCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "playPause":
                return PlayPauseCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "status":
                return StatusCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "createPlaylist":
                return CreatePlaylist.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getPlaylistName(), node, allUsersPlaylists);
            case "addRemoveInPlaylist":
                return AddRemoveInPlaylist.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getPlaylistId(), node);
            case "showPlaylists":
                return ShowPlaylists.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "like":
                return LikeCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "showPreferredSongs":
                return ShowPreferredSongs.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "repeat":
                return RepeatCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "shuffle":
                return ShuffleCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getSeed(), node);
            case "switchVisibility":
                return SwitchVisibilityCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getPlaylistId(), node);
            case "follow":
                return FollowPlaylistCommand.getInstance(inputcommand.getCommand(), inputcommand.
                        getUsername(), inputcommand.getTimestamp(), node);
            case "getTop5Songs":
                return GetTop5SongsCommand.getInstance(inputcommand.getCommand(), Main.songsList,
                        inputcommand.getTimestamp(), node);
            case "getTop5Playlists":
                return GetTop5Playlists.getInstance(inputcommand.getCommand(), node,
                        inputcommand.getTimestamp(), allUsersPlaylists);
            case "forward":
                return ForwardCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "backward":
                return BackwardCommand.getInstance(inputcommand.getCommand(), inputcommand.
                        getUsername(), inputcommand.getTimestamp(), node);
            case "next":
                return NextCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "prev":
                return PrevCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "switchConnectionStatus":
                return SwitchConnectionStatus.getInstance(inputcommand.getCommand(), inputcommand.
                        getUsername(), inputcommand.getTimestamp(), node);
            case "getOnlineUsers":
                return GetOnlineUsers.getInstance(inputcommand.getCommand(), node,
                        inputcommand.getTimestamp());
            case "addUser":
                return AddUser.getInstance(inputcommand.getCommand(), inputcommand.getTimestamp(),
                        inputcommand.getType(), inputcommand.getUsername(),
                        inputcommand.getAge(), inputcommand.getCity(), node);
            case "addAlbum":
                ArrayList<SongPlayInfo> albumSongList = new ArrayList<>();
                for (SongInput song : inputcommand.getSongs()) {
                    SongPlayInfo newSong = new SongPlayInfo();
                    newSong.setSong(song);
                    albumSongList.add(newSong);
                }
                return AddAlbum.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(),
                        inputcommand.getReleaseYear(), inputcommand.getDescription(),
                        albumSongList, node);
            case "showAlbums":
                return ShowAlbums.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node);
            case "printCurrentPage":
                return PrintCurrentPage.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "addEvent":
                return AddEvent.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(), node,
                        inputcommand.getDescription(), inputcommand.getDate());
            case "addMerch":
                return AddMerch.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(),
                        inputcommand.getDescription(), inputcommand.getPrice(), node);
            case "deleteUser":
                return DeleteUser.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node);
            case "getAllUsers":
                return GetAllUsers.getInstance(inputcommand.getCommand(), node,
                        inputcommand.getTimestamp());
            case "addPodcast":
                return AddPodcast.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(), node,
                        inputcommand.getEpisodes());
            case "addAnnouncement":
                return AddAnnouncement.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getName(), node, inputcommand.getDescription());
            case "removeAnnouncement":
                return RemoveAnnouncement.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getName(), node);
            case "showPodcasts":
                return ShowPodcasts.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "removeAlbum":
                return RemoveAlbum.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(),
                        inputcommand.getName(), node);
            case "changePage":
                return ChangePage.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node, inputcommand.getNextPage());
            case "removePodcast":
                return RemovePodcast.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(), node);
            case "removeEvent":
                return RemoveEvent.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(),
                        inputcommand.getTimestamp(), inputcommand.getName(), node);
            case "getTop5Albums":
                return GetTop5Albums.getInstance(inputcommand.getCommand(), node,
                        inputcommand.getTimestamp());
            case "getTop5Artists":
                return GetTop5Artists.getInstance(inputcommand.getCommand(), node,
                        inputcommand.getTimestamp());
            case "wrapped":
                return WrappedCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "endProgram":
                return EndProgram.getInstance(inputcommand.getCommand(), inputcommand.
                        getTimestamp(), node);
            case "buyMerch":
                return BuyMerch.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node, inputcommand.getName());
            case "seeMerch":
                return SeeMerch.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node);
            case "updateRecommendations":
                return UpdateRecommendations.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), inputcommand.
                                getRecommendationType(), node);
            case "previousPage":
                return PreviousPage.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node);
            case "nextPage":
                return NextPage.getInstance(inputcommand.getCommand(), inputcommand.getUsername(),
                        inputcommand.getTimestamp(), node);
            case "loadRecommendations":
                return LoadRecommendations.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node);
            case "subscribe":
                return SubscribeCommand.getInstance(inputcommand.getCommand(),
                        inputcommand.getUsername(), inputcommand.getTimestamp(), node);
            case "getNotifications":
                return GetNotifications.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node);
            case "buyPremium":
                return BuyPremium.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node);
            case "cancelPremium":
                return CancelPremium.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node);
            case "adBreak":
                return AdBreak.getInstance(inputcommand.getCommand(), inputcommand.
                                getUsername(), inputcommand.getTimestamp(), node,
                        inputcommand.getPrice());
            default:
                return null;
        }
    }
}
