package se.throwthebombservice.data;

import java.io.Serializable;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "gameUsers")
@XmlAccessorType (XmlAccessType.FIELD)
public class GameUsers implements Serializable{
	@XmlElement(name = "user")
	private TreeSet<User> userList;

	public GameUsers(){
		this.userList = new TreeSet<User>();
	}
	
	public User getUser(String name){
		for (User user : this.userList){
			if (user.getName().compareTo(name) == 0){
				return user;
			}
		}
		return null;
	}
	
	public boolean addUser(User user){
		return this.userList.add(user);
	}
	
	public TreeSet<User> getUserList() {
		return userList;
	}

	public void setUserList(TreeSet<User> invitedUserList) {
		this.userList = invitedUserList;
	}
}
