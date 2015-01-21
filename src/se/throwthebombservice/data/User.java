package se.throwthebombservice.data;

import java.io.*;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User implements Comparable<User>, Serializable{
	private String name;
	private String pwd;
	private Location location;
	private boolean online;
	private boolean alive;
	private boolean logedIn;
	private Friends friends;
	private PowerUp powerUp;
	private Game currentGame;
//	private TreeSet<Game> invitedGamesList;
	private String chosenInvitedGame;
	
	public User(){
		this.name = "DefaultUser";
		this.friends = new Friends();
		this.location = new Location();
	}
	
	public boolean addFriend(User user){
		return this.friends.addFriend(user);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	public boolean isLogedIn() {
		return logedIn;
	}
	public void setLogedIn(boolean logedIn) {
		this.logedIn = logedIn;
	}
	public PowerUp getPowerUp() {
		return powerUp;
	}
	public void setPowerUp(PowerUp powerUp) {
		this.powerUp = powerUp;
	}
	public Game getCurrentGame() {
		return currentGame;
	}
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
//	public TreeSet<Game> getInvitedGamesList() {
//		return invitedGamesList;
//	}
//	public void setInvitedGamesList(TreeSet<Game> invitedGamesList) {
//		this.invitedGamesList = invitedGamesList;
//	}
	public String getChosenInvitedGame() {
		return chosenInvitedGame;
	}
	public void setChosenInvitedGame(String chosenInvitedGame) {
		this.chosenInvitedGame = chosenInvitedGame;
	}
	public Friends getFriends() {
		return friends;
	}

	public void setFriends(Friends friends) {
		this.friends = friends;
	}
	@Override
	public int compareTo(User o) {
		return this.name.compareTo(o.getName());
	}
	
	/**
	* This method makes a "deep clone" of any object it is given.
	*/
	public static User getClonedUserWithoutCycle(Object object){
		User retUser = (User) deepClone(object);
		Iterator<User> iterator = retUser.getFriends().getFriendsList().iterator();
		User user = null;
		while (iterator.hasNext()){
			user = iterator.next();
			user.setFriends(null);
		}
//		retUser.setInvitedGamesList(null);
		return retUser;
	}
	
	private static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		    return null;
		}
	}

}
