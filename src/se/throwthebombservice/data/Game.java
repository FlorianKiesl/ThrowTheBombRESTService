package se.throwthebombservice.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="game")
public class Game implements Comparable<Game>, Serializable {
	private String name;
	private GameUsers gameUsers;
	private User userWithBomb;
	private InvitedUsers invitedUsers;
	private int chosenRadius;
	private int chosenDuration;
	private int explusionDuration;
	private boolean run = false;
	
	private BombState bombState;
	
	private long startTimeinMs;
//	private long gameStartDurationInMs;
	private long timeToGameStart;
	private long timeToNextExplusion;
	
	public Game(){
		this.name = "Default-Game";
		this.gameUsers = new GameUsers();
		this.invitedUsers = new InvitedUsers();
		this.bombState = BombState.not_exploded;
		this.timeToGameStart = 30*1000;
		
		this.explusionDuration = 45*1000;
		this.timeToNextExplusion = this.explusionDuration + this.timeToGameStart;
	}
	
	public boolean joinGame(String userName){
		User user = this.isUserInvitved(userName);
		if (user != null){
			user.setAlive(true);
			user.setPowerUp(PowerUp.NoPowerUp);
			user.setChosenInvitedGame(this.getName());
			user.setCurrentGame(this);
			return this.getGameUsers().addUser(user);
		}
		return false;
	}
	
	public boolean addUser(User user){
		return this.gameUsers.addUser(user);
	}
	
	public boolean inviteUser(User user){
		return this.invitedUsers.addUser(user);
	}
	
	public User isUserInvitved(String userName){
		for(User user : this.getInvitedUsers().getUserList()){
			if (user.getName().compareTo(userName) == 0){
				return user;
			}
		}
		return null;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public GameUsers getGameUsers() {
		return gameUsers;
	}

	public void setGameUsers(GameUsers users) {
		this.gameUsers = users;
	}

	public User getUserWithBomb() {
		return userWithBomb;
	}

	public synchronized void setUserWithBomb(User userWithBomb) {
		this.userWithBomb = userWithBomb;
	}

	public InvitedUsers getInvitedUsers() {
		return invitedUsers;
	}

	public void setInvitedUsers(InvitedUsers invitedUser) {
		this.invitedUsers = invitedUser;
	}

	public int getChosenRadius() {
		return chosenRadius;
	}

	public void setChosenRadius(int chosenRadius) {
		this.chosenRadius = chosenRadius;
	}

	public int getChosenDuration() {
		return chosenDuration;
	}

	public void setChosenDuration(int chosenDuration) {
		this.chosenDuration = chosenDuration;
	}
//
//	public long getDuration() {
//		return gameStartDurationInMs;
//	}
//
//	public void setDuration(long duration) {
//		this.gameStartDurationInMs = duration;
//	}

	public long getStartTime() {
		return startTimeinMs;
	}

	public void setStartTime(long startTime) {
		this.startTimeinMs = startTime;
	}

	public BombState getBombState() {
		return bombState;
	}

	public void setBombState(BombState bombState) {
		this.bombState = bombState;
	}

	@Override
	public int compareTo(Game o) {
		return this.name.compareTo(o.getName());
	}
	
	public long getTimeToGameStart() {
		return timeToGameStart;
	}

	public void setTimeToGameStart(long timeToGameStart) {
		this.timeToGameStart = timeToGameStart;
	}
	
	public long getTimeToNextExplusion() {
		return timeToNextExplusion;
	}

	public void setTimeToNextExplusion(long timeToNextExplusion) {
		this.timeToNextExplusion = timeToNextExplusion;
	}
	
	public long getExplusionDuration() {
		return explusionDuration;
	}
	
	public void setGameToRun() {
		this.run = true;
	}
	
	public boolean isGameRunning() {
		return this.run;
	}
	
	public int getUsersAliveCount() {
		
		int count = 0;
		Iterator<User> iterator = this.getGameUsers().getUserList().iterator();
		while (iterator.hasNext()){
			if(iterator.next().isAlive())
		      count++;
		}
		
		return count;
	}
	
	public boolean bombExploded() {
		
		User u;
		boolean ret = false;
		
		//set alive to false for user with bomb		
		Iterator<User> iterator = this.getGameUsers().getUserList().iterator();
		while (iterator.hasNext()){
			
			u = iterator.next();
			if(u.compareTo(userWithBomb) == 0) {
				u.setAlive(false);
				System.out.println("Player " + u.getName() + " is out!");
				ret = true;
				break;
			}				
		}
		
		//get the next player		
		Random randomGenerator = new Random();
		int numberOfUserInList = randomGenerator.nextInt(this.getUsersAliveCount());
		
		iterator = this.getGameUsers().getUserList().iterator();
		while (iterator.hasNext()){
			
			u = iterator.next();
			if(u.isAlive()) {
				if(numberOfUserInList <= 0) {
					
					userWithBomb = u;
					System.out.println("Player " + userWithBomb.getName() + " has the bomb!");
					return true;
				}
				numberOfUserInList--;
			}			
		}
		
		return ret;
	}

//	public long timeToGameStart(){
//		return this.getDuration() - (System.currentTimeMillis() - this.startTimeinMs);
//	}
	
	public static Game getClonedGameWithoutCycles(Object object){
		Game retGame = (Game) Game.deepClone(object);
		Iterator<User> iterator = retGame.getGameUsers().getUserList().iterator();
		while (iterator.hasNext()){
			iterator.next().setFriends(new Friends());
		}
		iterator = retGame.getInvitedUsers().getUserList().iterator();
		while (iterator.hasNext()){
			iterator.next().setFriends(new Friends());
		}
		if (retGame.getUserWithBomb() != null){
			retGame.setUserWithBomb(null);
		}
		retGame.setGameUsers(null);
		retGame.setInvitedUsers(null);
		return retGame;
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
