package se.throwthebombservice.data;

import java.io.Serializable;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "friends")
@XmlAccessorType (XmlAccessType.FIELD)
public class Friends implements Serializable{
	
	@XmlElement(name = "user")
	private TreeSet<User> friendsList;
	
	public Friends(){
		this.friendsList = new TreeSet<User>();
	}
	
	
	public boolean addFriend(User user){
		return this.friendsList.add(user);
	}

	public TreeSet<User> getFriendsList() {
		return friendsList;
	}
	public void setFriendsList(TreeSet<User> friendsList) {
		this.friendsList = friendsList;
	}
	
}
