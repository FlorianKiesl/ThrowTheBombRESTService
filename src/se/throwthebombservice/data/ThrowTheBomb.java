package se.throwthebombservice.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class ThrowTheBomb {
	
	public static ThrowTheBomb oThrowTheBomb;
	
	private TreeSet<Game> gameList;
	private TreeSet<User> userList;
	
	private ThrowTheBomb(){
		this.gameList = new TreeSet<Game>();
		this.userList = new TreeSet<User>();
	}
	
	public static ThrowTheBomb getInstance(){
		if (oThrowTheBomb == null){
			oThrowTheBomb = new ThrowTheBomb();
			oThrowTheBomb.loadDefaultData();
		}
		return oThrowTheBomb;
	}
	
	public Game getGame(String name){
		Game game;
		Iterator<Game> iterator = this.gameList.iterator();
		while (iterator.hasNext()){
			game = iterator.next();
			if (game.getName().compareTo(name) == 0){
				return game;
			}
		}
		return null;
	}
	
	public List<Game> getInvitedGameNamesForUser(String userName){
		List<Game> games = new ArrayList<Game>();
		Game game;
		Iterator<Game> iteratorGame = this.gameList.iterator();
		while (iteratorGame.hasNext()){
			game = iteratorGame.next();
			if (game.getInvitedUsers().hasUser(userName)){
				games.add(Game.getClonedGameWithoutCycles(game));
			}
		}
		
		return games;
	}
	
	public User getUser(String name){
		User user;
		Iterator<User> iterator = this.userList.iterator();
		while (iterator.hasNext()){
			user = iterator.next();
			if (user.getName().compareTo(name) == 0){
				return user;
			}
		}
		return null;
	}
	
	public boolean addGame(Game game){
		return this.gameList.add(game);
	}
	
	public boolean removeGame(String name){
		Iterator<Game> iterator = this.gameList.iterator();
		Game game;
		while (iterator.hasNext()){
			game = iterator.next();
			if (game.getName().compareTo(name) == 0){
				return this.gameList.remove(game);
			}
		}
		return false;
	}
	
	public TreeSet<Game> getGameList() {
		return gameList;
	}

	public void setGameList(TreeSet<Game> gameList) {
		this.gameList = gameList;
	}
	
	public TreeSet<User> getUserList() {
		return userList;
	}

	public void setUserList(TreeSet<User> userList) {
		this.userList = userList;
	}
	
	public boolean addUser(User user){
		return this.userList.add(user);
	}

	private void loadDefaultData(){
		Game newGame = new Game();
		newGame.setName("JKU");
		getInstance().addGame(newGame);
		newGame = new Game();
		newGame.setName("Linz");
		getInstance().addGame(newGame);
		newGame = new Game();
		newGame.setName("SciencePark");
		getInstance().addGame(newGame);
		
		Location location;
		
		User newUser = new User();
		newUser.setName("Florian");
		newUser.setPwd("se2014");
		newUser.setLogedIn(false);
		newUser.setOnline(false);
		newUser.setPowerUp(PowerUp.NoPowerUp);
		getInstance().addUser(newUser);
		loadDefaultLocation(newUser);
		
		newUser = new User();
		newUser.setName("Johannes");
		newUser.setPwd("se2014");
		newUser.setLogedIn(false);
		newUser.setOnline(false);
		newUser.setPowerUp(PowerUp.NoPowerUp);
		getInstance().addUser(newUser);
		loadDefaultLocation(newUser);
		
		newUser = new User();
		newUser.setName("Alexander");
		newUser.setPwd("se2014");
		newUser.setLogedIn(false);
		newUser.setOnline(false);
		newUser.setPowerUp(PowerUp.NoPowerUp);
		getInstance().addUser(newUser);
		loadDefaultLocation(newUser);
		
//		newUser = new User();
//		newUser.setName("Hugo");
//		newUser.setPwd("se2014");
//		newUser.setLogedIn(false);
//		newUser.setOnline(false);
//		newUser.setPowerUp(PowerUp.NoPowerUp);
//		getInstance().addUser(newUser);		
//		
//		newUser = new User();
//		newUser.setName("Bert");
//		newUser.setPwd("se2014");
//		newUser.setLogedIn(false);
//		newUser.setOnline(false);
//		newUser.setPowerUp(PowerUp.NoPowerUp);
//		getInstance().addUser(newUser);	
//
//		newUser = new User();
//		newUser.setName("Katze");
//		newUser.setPwd("se2014");
//		newUser.setLogedIn(false);
//		newUser.setOnline(false);
//		newUser.setPowerUp(PowerUp.NoPowerUp);
//		getInstance().addUser(newUser);
		
		getInstance().getUser("Florian").addFriend(getInstance().getUser("Alexander"));
		getInstance().getUser("Florian").addFriend(getInstance().getUser("Johannes"));
		
		//Keine Zyklen in Soap
		getInstance().getUser("Alexander").addFriend(getInstance().getUser("Florian"));
		getInstance().getUser("Johannes").addFriend(getInstance().getUser("Alexander"));
		
		this.getGame("JKU").inviteUser(getInstance().getUser("Florian"));
//		this.getGame("JKU").addUser(getInstance().getUser("Florian"));
		this.getGame("JKU").inviteUser(getInstance().getUser("Johannes"));
		this.getGame("JKU").inviteUser(getInstance().getUser("Alexander"));
//		this.getGame("JKU").addUser(getInstance().getUser("Johannes"));
//		this.getGame("JKU").addUser(getInstance().getUser("Alexander"));
//		this.getGame("JKU").setUserWithBomb(getInstance().getUser("Florian"));
		
		this.getGame("Keplergebäude").inviteUser(getInstance().getUser("Florian"));
		
		
	}
	
	public void loadDefaultLocation(User user){
		Location location;
		if (user.getName().compareTo("Florian") == 0){
			location = new Location();
			//Wien
			location.setLatitude(48.209);
			location.setLongitude(16.37);
			getUser("Florian").setLocation(location);
		}
		else if (user.getName().compareTo("Johannes") == 0){
			location = new Location();
			//Klagenfurt
			location.setLatitude(46.63);
			location.setLongitude(14.31);
			this.getUser("Johannes").setLocation(location);
		}
		else if (user.getName().compareTo("Alexander") == 0){
			location = new Location();
			//Linz
			location.setLatitude(48.305);
			location.setLongitude(14.286);
			this.getUser("Alexander").setLocation(location);
		}
	}
}
