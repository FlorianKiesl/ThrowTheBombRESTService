package se.throwthebombservice.data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invitedUsers")
@XmlAccessorType (XmlAccessType.FIELD)
public class InvitedUsers implements Serializable{
	
	@XmlElement(name = "user")
	private TreeSet<User> userList;

	public InvitedUsers(){
		this.userList = new TreeSet<User>();
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
	
	public boolean hasUser(String userName){
		Iterator<User> iterator = this.userList.iterator();
		while(iterator.hasNext()){
			if (iterator.next().getName().compareTo(userName) == 0){
				return true;
			}
		}
		return false;
	}
}
