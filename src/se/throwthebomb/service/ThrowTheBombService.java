package se.throwthebomb.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.Produces;  
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType; 

import se.throwthebombservice.data.Game;
import se.throwthebombservice.data.Location;
import se.throwthebombservice.data.MathVector;
import se.throwthebombservice.data.ThrowTheBomb;
import se.throwthebombservice.data.User;

@Path("/ThrowTheBomb")
public class ThrowTheBombService {
	
	@GET
	@Path("/UpdateUserLocation")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean updateUserLocation(@QueryParam("username") String userName
			, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude){
		User user = ThrowTheBomb.getInstance().getUser(userName);
		if (user != null){
			Location location = user.getLocation();
			location.setLongitude(longitude);
			location.setLatitude(latitude);
			return true;
		}
		return false;
	}
	
	@GET
	@Path("/ThrowBomb")
	@Produces(MediaType.TEXT_PLAIN)
	public  boolean throwBomb(@QueryParam("username") String userName			
			, @QueryParam("XMathVector") double xMathVector, @QueryParam("YMathVector") double yMathVector
			, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude){	
		
		System.out.println("bomb was throwen from " + userName);
		User userFrom = ThrowTheBomb.getInstance().getUser(userName);		
		
		Location location = userFrom.getLocation();
		location.setLongitude(longitude);
		location.setLatitude(latitude);
		
		Game game = userFrom.getCurrentGame();
		if (game == null) return false;
		if (game.getUserWithBomb() == null) return false;
		if (game.getUserWithBomb().getName().compareTo(userFrom.getName()) != 0) return false;
		
		TreeSet<User> users = game.getGameUsers().getUserList();
		ArrayList<User> usersAsList = new ArrayList<User>(users);
		usersAsList.remove(userFrom);
		
		Location bombLocation = userFrom.getLocation();
		
		double maxAngle = 30;
		User futureUserWithBomb = null;
		double smallestAngle = maxAngle;
		
		MathVector viewVector = new MathVector(xMathVector, yMathVector);
		
		for (User u : usersAsList) {
			System.out.println("User: " + u.getName());
			if (u.isAlive()){
				Location otherUserLocation = u.getLocation();
				MathVector directVector = new MathVector(Math.toRadians(otherUserLocation.getLongitude()) - Math.toRadians(bombLocation.getLongitude()), Math.toRadians(otherUserLocation.getLatitude()) - Math.toRadians(bombLocation.getLatitude()));
				
			    double angle = Math.toDegrees(Math.acos(((viewVector.getX() * directVector.getX()) + (viewVector.getY() * directVector.getY())) / directVector.getNorm()));
			    System.out.println("angle: " + angle);
			    if ((angle < maxAngle) && (angle < smallestAngle)) {
			    	
			    	smallestAngle = angle;
			    	futureUserWithBomb = u;
			    }
			}
		}
		
		if (futureUserWithBomb != null) {
			game.setUserWithBomb(futureUserWithBomb);
			System.out.println("future: " + futureUserWithBomb.getName());		
			return true;
		}
		return false;
	}
	
	@GET
	@Path("/FindBomb")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean findBomb(@QueryParam("username") String userName
		, @QueryParam("XMathVector") double xMathVector, @QueryParam("YMathVector") double yMathVector
		, @QueryParam("longitude") double longitude, @QueryParam("latitude") double latitude){
		User user = ThrowTheBomb.getInstance().getUser(userName);
		
//		ToDo: Calculate User for Bomb. Same Method as ThrowBomb
		
		return false;
	}
	
	
	@GET
	@Path("/Login")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean loginUser(@QueryParam("name") String userName,@QueryParam("password") String password){
		try{
			
			User user = ThrowTheBomb.getInstance().getUser(userName);
			if (user == null) {
				return false;
			}
			else {
				if (user.getPwd().compareTo(password) == 0){
					user.setLogedIn(true);
					ThrowTheBomb.getInstance().loadDefaultLocation(user);
					return true;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	@GET
	@Path("/JoinGame")
	@Produces(MediaType.TEXT_PLAIN)
    public boolean joinGame(@QueryParam("gamename") String gameName, @QueryParam("username") String userName){
		Game game = ThrowTheBomb.getInstance().getGame(gameName);
		boolean ret = false;
		if (game == null){
			ret = false;
		}
		else{
			ret = game.joinGame(userName);
			if (ret) System.out.println("User angemeldet: " + userName);
			if (ret == true && game.getGameUsers().getUserList().size() == 1){
				
				Thread startGameThread = new Thread(){

					@Override
					public synchronized void start() {
						super.start();
						game.setStartTime(System.currentTimeMillis());
					}

					@Override
					public synchronized void run() {
						super.run();
						try {
							System.out.println("Start: " + game.getStartTime());
							while(game.getTimeToGameStart() > 0 || game.getUsersAliveCount() > 1){ //runs until the game is over
								Thread.sleep(1000);
								game.setTimeToGameStart(game.getTimeToGameStart() - 1000);
								//System.out.println("Time to/since game start: " + game.getTimeToGameStart());
								game.setTimeToNextExplusion(game.getTimeToNextExplusion() - 1000);
								//System.out.println("Time to next bomb explode: " + game.getTimeToNextExplusion());
								
								if(!game.isGameRunning() && game.getTimeToGameStart() <= 0) {
									
									game.setGameToRun();
									if(game.getUsersAliveCount() >= 1)
										game.setUserWithBomb(game.getGameUsers().getUserList().first());
								}
								else if(game.getTimeToNextExplusion() <= 0) {
									
									game.bombExploded();
									game.setTimeToNextExplusion(game.getExplusionDuration());
								}							
							}
							
							
							System.out.println("UserWithBomb: " + game.getUserWithBomb().getName());
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				};
				
				startGameThread.start();
			}
		}
    	return ret;
    }
	
	@GET
	@Path("/GetInvitedGames")
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_XML)
	public List<Game> getInvitedGames(@QueryParam("username") String userName){
		return ThrowTheBomb.getInstance().getInvitedGameNamesForUser(userName);
	}
	
	@GET
	@Path("/TimeToGameStart")
	@Produces(MediaType.TEXT_PLAIN)
	public long timeToGameStartInMs(@QueryParam("username") String username){
		Game game = ThrowTheBomb.getInstance().getUser(username).getCurrentGame();
		if (game == null){
			return 10*60*1000;
		}
		
		return game.getTimeToGameStart();
	}
	
	@GET
	@Path("/UsersAliveInGame")
	@Produces(MediaType.TEXT_PLAIN)
	public int usersAliveInGame(@QueryParam("username") String username){
		Game game = ThrowTheBomb.getInstance().getUser(username).getCurrentGame();
		if (game == null)
		  return 0;
		
		return game.getUsersAliveCount();
	}
	
	@GET
	@Path("/IsBombByUser")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean isBombByUser(@QueryParam("username") String userName){
		User user = ThrowTheBomb.getInstance().getUser(userName);
		if (user == null){
			return false;
		}
		
		User userWithBomb = user.getCurrentGame().getUserWithBomb();
		
		if (userWithBomb == null){
			return false;
		}
		
		if (userWithBomb.getName().compareTo(userName) == 0){
			return true;
		}
		
		return false;
	}
	
	@GET
	@Path("/IsUserAliveInGame")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean isUserAliveInGame(@QueryParam("username") String userName){
		User user = ThrowTheBomb.getInstance().getUser(userName);
		if (user == null){
			return false;
		}
		
		return user.isAlive();
	}
	
//	@GET
//	@Path("/UserExploaded")
//	@Produces(MediaType.TEXT_PLAIN)
//	public boolean userExploaded(@QueryParam("gamename") String gameName, @QueryParam("username") String userName){
//		Game game = ThrowTheBomb.getInstance().getGame(gameName);
//		if (game != null){
//			//ToDo: Wie bekommt jeder explosion mit?
//			game.setBombState(BombState.exploded);
//			User user = ThrowTheBomb.getInstance().getGame(gameName).getGameUsers().getUser(userName);
//			if (user != null){
//				user.setAlive(false);
//				return true;
//			}
//		}
//		return false;
//	}
	
//	@GET
//	@Path("/UserUtilizePowerUp")
//	@Produces(MediaType.TEXT_PLAIN)
//	public boolean updateUserPowerUp(@QueryParam("gamename") String gameName, @QueryParam("username") String userName, String powerUp){
//		User user = ThrowTheBomb.getInstance().getGame(gameName).getGameUsers().getUser(userName);
//		if (user != null){
//			if (powerUp.compareTo("NoPowerUp") == 0){
//				user.setPowerUp(PowerUp.NoPowerUp);
//			}
//			else if (powerUp.compareTo("GHOST") == 0){
//				user.setPowerUp(PowerUp.GHOST);
//			}
//			else if (powerUp.compareTo("SHIELD") == 0){
//				user.setPowerUp(PowerUp.SHIELD);
//			}
//			else if (powerUp.compareTo("TARGET") == 0){
//				user.setPowerUp(PowerUp.TARGET);
//			}
//			return true;
//		}
//		return false;
//	}
	
	/*
	 * Get Game Object. Users of the Game Object have no friends because of cycle problem.
	 */
//	@GET
//	@Path("/GetGame")
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//	public Game getGame(@QueryParam("name") String name){
//		Game game = null;
//		try{
//			game = Game.getClonedGameWithoutCycles(ThrowTheBomb.getInstance().getGame(name));
//			return game;
//			
//		}catch (Exception exc){
//			exc.printStackTrace();
//		}
//		return null;
//	}
	
//	@GET
//	@Path("/CreateGame")
//	@Produces(MediaType.TEXT_PLAIN)
//	public boolean createGame(@QueryParam("name") String name, 
//			@QueryParam("radius") int radius, @QueryParam("chosenDuration") int chosenDuration){
//		Game game = new Game();
//		game.setName(name);;
//		game.setChosenRadius(radius);;
//		game.setChosenDuration(chosenDuration);
//		
//		return ThrowTheBomb.getInstance().addGame(game);
//	}

//	@GET
//	@Path("/InviteUserToGame")
//	@Produces(MediaType.TEXT_PLAIN)
//    public boolean inviteUserToGame(@QueryParam("gamename") String gameName, @QueryParam("username") String userName){
//    	User user = ThrowTheBomb.getInstance().getUser(userName);
//    	if (user != null){
//    		ThrowTheBomb.getInstance().getGame(gameName).inviteUser(user);
//    		return true;
//    	}
//    	return false;
//    }
	
//	@GET
//	@Path("/DeleteGame")
//	@Produces(MediaType.TEXT_PLAIN)
//	public boolean deleteGame(@QueryParam("name") String name){
//		
//		return ThrowTheBomb.getInstance().removeGame(name);
//	}
//	
	@GET
	@Path("/GetAllUsers")
	@Produces(MediaType.APPLICATION_XML)
	public List<User> getAllUsers(){
		List<User> userList = new ArrayList<User>();
		for(User user : ThrowTheBomb.getInstance().getUserList()){
			userList.add(User.getClonedUserWithoutCycle(user));
		}
		return userList;
	}

//    @POST
//    @Path("/CreateUser")
//    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    @Produces(MediaType.TEXT_PLAIN)
//    public boolean createUser(User user){
//    	return ThrowTheBomb.getInstance().addUser(user);
//    }
    
    
}
