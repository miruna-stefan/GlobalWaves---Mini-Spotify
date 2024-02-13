STEFAN MIRUNA ANDREEA 324CA

# OOP Project GlobalWaves

# Design Patterns used:

1. Observer

- i used this behavioural design pattern in the implementation of the 
notification system: the artist and host classes implement the interface 
Subject, as they are observed by the users that have subscribed to them, whie
the NormalUser implements the interface Observer, as it is the one who 
observers the changes of the artist / host.

- each artist / host class has a field "subscribers", which is an arraylist 
that keeps all the normal users that have subscribed to them. This is basically
the list of observers. Each normal user has a field called "notifications", 
which keeps a list of ObjectNodes, each objectNode having 2 elements: the name
of the notification and the description of the notification.

- The Subject interface has 3 methods (addObserver, removeObserver and 
notifyObservers), which are used as follows: Each time a user subscribes to an
artist, that user is added to the observers list by calling the method 
addObserver. If the user was already a subscriber of the artist, it is 
eliminated from that list with the help of the removeObserver method. When the
artist adds a new merch item or event, a new ObjectNode with the details of the
notification is created. In order to add it to the notification list of all the
artist's subscribers, the method notifyObservers is called. Its role is to call
the updateNotifications function for each user in the observers list of the 
artist. Then, the updateNotifications method (the only method in the observer 
interface, implemented in the NormalUser class) adds the new notification 
ObjectNode to the user's list of notifications, which will be displayed when 
receiving the getNotifications command and then cleared.

2. Visitor

- this behavioural design pattern was used in 2 different places in code:

	a) for the deleteUser command
	
	- the visitor pattern is used to help us call the correct deletion 
	function according to the type of the user that we want to delete.
	
	- the VisitorDeletion interface contains the signatures of the 
	overloaded methods "canBeDeleted", with different parameters, depending
	on the type of user that we will aim to delete. Inside the DeleteUser 
	class, which is instantiated when the DeleteUser command is received, 
	i have created the class DeletionExecutionAccordingToUserType, which 
	implements the VisitorDeletion interface and therefore contains the 
	implementation of the canDeleteUser method for each user type. (I have 
	decided to create this class in order to better fragment the code so 
	that it encapsulates a single functionality and I chose to make it an
	inner class of the DeleteUser class because an object of this inner
	class can only exist if the outer class DeleteUser has already been 
	instatiated
	
	- the VisitableDeletion interface only contains the signature of the 
	acceptDeletion method, that is further overriden in the classes that 
	implement this interface, namely all the subclasses of the GeneralUser
	class (NormalUser, Artist, Host).
	
	b) for the wrapped command
	
	- the flow is similar to what i have explained at point a). I have 
	created the inner class WrappedExecutionAccordingToUSerType inside the
	WrappedCommand class. This inner class implements the VisitorWrapped 
	interface, which contains the overloaded methods getWrappedResultNode: 
	one has a normal user as parameter, other has an artist as parameter 
	and the last one has a host as parameter. These methods (whose 
	implementation can be found in WrappedExecutionAccordingToUSerType 
	class, only prepare the resulted objectNode for being printed in a 
	specific format; the methods that actually perform operations to obtain
	the data are implemented in the wrappedCommand class (outside
	WrappedExecutionAccordingToUSerType). Again, the pair interface, 
	VisitableWrapped, is implemented by all the subclasses of the 
	GeneralUser class (NormalUser, Artist, Host) and only contains the 
	acceptWrapped method.
	
3. Factory

- this creational pattern facilitates the instantiation of the command classes,
according to the  received inputcommand. I have created a class CommandFactory,
dedicated to the creation of the commands. This class only contains a static 
method createCommand, which identifies the current inputcommand and gets the 
instance of the specific class. The trick is that the createCommand method 
returns an object of type GeneralCommand. Going back to the first level of the
inheritance chain, you will notice that each and every command class basically 
extends the abstract class GeneralCommand. Therefore, we don't need to know the
exact subclass correspondent to the command, but it is enough to return a 
GeneralClass object.

- the advantage of this design pattern is the centralzation and the 
encapsulation of the command objects creation in one place in the program, so 
that we can isolate the instance of the object of a specific type from its 
actual creation.

4. Command

- this behavioural design pattern helped me manage working with all the 
commands received as input. All the commands are kept in the inputCommands 
list, whether they were read from a json file or manually added (this is the
case of the endProgram command, which was added at the end of this list). In
order to implement this design pattern, I have created a class CommandInvoker,
whose only method has the role to go through each element of the inputCommands
list, get the instance of the correspondent class and then call the execute 
method and add the resulted node to the outputs list. All command classes 
implement the Command interface, which only has one method: execute().

5. Singleton

- all command classes have been made singleton, so that we don't crete a new 
object each time a command is invoked, but reuse the last object, after getting
the fields updated.


# General flow and code organization:

I decided to create a class for each command that is received. The commands 
are read from json files and stored in the inputCommands array. We will iterate
throush this array in order to tackle all the commands received as input. I 
have organized the classes as follows:

- "fileio.input" package: 
	- "audioEntities" package: 
		- contains all the entities that can be loaded in the player 
		and listened to by the users
		- besides the files that were already here in the 1st stage's
		given framework (EpisodeInput, SongInput, PodcastInput) , I 
		added in this package my own classes (Album, Playlist, 
		PodcastPlayInfo, SongPlayInfo)
		
	- "entitiesForArtist" package:
		- contains the classes that i have created for the entities 
		specific only to the artist: Merch and Event
		
	- "entitiesForHost" package:
		- contains the class that i have created for the only entity
		specific just to the host: Announcement
		
	- "Pagination" package:
		- as there are 4 possible types of pages in the app, i have 
		created 4 classes, one for a specific type of page (ArtistPage,
		HomePage, HostPage, LikedSongsPage). All of them extend the 
		abstract superclass Page, which has an abstract method 
		(pageToString), that is overriden by each of its subclasses,
		in accordance with the specific format of the page type.
		- each normal user will retain a list of objects of type Page,
		that is going to be the history of the page navigation
		
	-"wrappedEntities" package:
		- in order to get the correct data for the wrapped command,
		i have created some classes (WrappedSong, WrappedArtist, etc.),
		with the purpose of making the association between the element
		that we are making our statistics for (e.g. for WrappedGenres,
		we are making statistics based on genre) and the number of 
		listens. All these classes have something in common: the field 
		listens, so they all extend the class GeneralEntityWrapped, 
		which only has the field listens. Each normal user will have 
		lists of elements of these types, that will be constantly 
		updated in order to keep track of these statistics.
		- the class MonetizedSong follows the same logic, as it makes
		the association between the song and its monetization. Each 
		artist is going to have a list of objects of type 
		MonetizaedSong to keep evidence of the monetization.
		
	- InputCommands class: 
		- a class containing all the possible fields that could be read
		from the json file

	- Filters: 
		- a class containing all the possible filters that could be 
		applied for the search command
	
	- UserInput
	
	- LibraryInput
		

- "users" package: - contains 2 interfaces and 4 classes: one class for each 
type of user (Normal user, artist and host), each of them inheriting the 
GeneralUser class:
	- GeneralUser: 
		- the superclass containing only the general attributes that
		each user must have, regardless of its type;
		- this class is inherited by all the 3 more particular classes,
		adapted to each user.
		
	- NormalUser:
		- subclass of the GeneralUser class
		- particularization of the GeneralUser class, containing fields
		and methods that are only specific to the NormalUser type of
		user
	
	- Artist: 
		- the same concept as the Normal User class, but for the Artist
		 type of user
		
	- Host:
		- the same concept as the Normal User class, but for the Host
		 type of user
		 
	- Observer and Subject interfaces
		- i have explained their functionality when discussing the 
		Observer Design Pattern
		
- "main" package:
	- "commandsHandling" package:
		- "Command" interface:
			- used for the implementation of the Command design 
			pattern
		- "CommandFactory" class:
			- used for the implementation of the factory design 
			pattern
		- "CommandInvoker" class:
			- also used for the implementation of the command 
			design pattern
		- "GeneralCommand" class:
			- contains only the most general fields of a command
			- It will be further inherited by the subclasses 
		StandardAdminCommand,	StandardArtistCommand, 
		StandardHostCommand, StandardStatisticsCommand, 
		StandardCommandForUserPlayer, that will customize the Command
		superclass by adding fields and methods that are only 
		particular to that specific type of command.
			- it is an abstract class as it contains the abstract 
			method execute(), which will be implemented by every 
			particular command class
			
	- Main class:
		- entry point of the program
		- added 5 static fields in this class: a list with all the 
		normal users, a list with all the artists, a list with all the
		hosts, a list containg all the songs in the app and a list 
		containing all the podcasts in the app.
		
	- adminCommands package:
		- this package brings together all the classes created for the 
		implementeation of each command that could be given by the 
		admin: AddUser, DeleteUser, ShowAlbums, ShowPodcasts
		- StandardAdminCommmand class:
			- inherits the GeneralCommand superclass and adds 
			functionalities that are only spcific to the commands
			in this package (admin commands)
			- this class is inherited by all the subclasses in this
			package
		- VisitorDeletion and VisitableDeletion interfaces: 
			- pair interfaces used for the user deletion
			
	- "artistCommands" package:
		- this package encompasses all the classes created for each 
		command that can be given by an user of type artist: AddAlbum,
		AddEvent, AddMerch, RemoveAlbum, RemoveEvent
		- StandardArtistCommand class:
			- inherits the Command superclass and adds 
			functionalities that are only spcific to the commands
			in this package (artist commands)
			- this class is inherited by all the subclasses in this
			package
	
	- "hostCommands" package
		- this package contains all the classes created for each 
		command that can be given by an user of type host:
		AddAnnouncement, AddPodcast, RemovePodcast, RemoveAnnouncement.
		- StandardHostCommand class:
			- inherits the Command superclass and adds 
			functionalities that are only spcific to the commands
			in this package (host commands)
			- this class is inherited by all the subclasses in this
			package
			
	- "statisticsCommands" package:
		- this package brings together all the classes created for the 
		commands that aim at displaying statistics: GetAllUsers, 
		GetOnlineUsers, GetTop5Albums, GetTop5Artists, 
		GetTop5Playlists, GetTop5SongsCommand, WrappedCommand,
		updateRecommendations
		- StandardStatisticsCommand class:
			- inherits the Command superclass and adds 
			functionalities that are only spcific to the commands
			in this package (statistics commands)
			- this class is inherited by all the subclasses in this
			package
		- VisitableWrapped and VisitorWrapped interfaces:
			- used for the visitor pattern implementation
			
	- "userCommands" package:
		- this package contains all the classes created for the 
		commands that can be given by a normal user: 
		AddRemoveInPlaylist, BackwardCommand, ChangePage, 
		CreatePlaylist, FollowPlaylistCommand, ForwardCommand, 
		LikeCommand, LoadCommand, NextCommand, PlayPauseCommand, 
		PrevCommand, PrintCurrentPage, RepeatCommand, SearchCommand,
		SelecteCommand, ShowPlaylists, ShowPrefferedSongs, 
		ShuffleCommand, StandardCommandForUserPlayer, StatusCommand,
		SwitchConnectionStatus, SwitchVisibilityCommand, etc. They are
		further organized in packages:
			- "PageNavigationCommands" package was designed for the
			commands that need browsing through the page history
			-"playerCommands" package contains all the commands 
			that modify the player
			-"userPlaylistHandling" package includes operations 
			with an user's playlists
		- StandardStatisticsCommand class:
			- inherits the Command superclass and adds 
			functionalities that are only spcific to the commands
			in this package (statistics commands)
			- this class is inherited by all the subclasses in this
			package
			
	- EndCommand: this command doesn't belong to any of the mentioned 
	categories because it is not instantiated as a result of receiving a 
	specific input, but it is particularly created and added at the end of
	the inputcommands list.
