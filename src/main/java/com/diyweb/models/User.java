package com.diyweb.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.jasypt.util.password.StrongPasswordEncryptor;

import internal.com.sun.istack.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Entity class that's used to persist user data.<br/>
 * Contains references to posts and comments made by this user.<br/>
 * isVerified field shows whether user e-mail was verified or not.
 * @author erick
 */
@Entity
@Table(name = "DiyUser")
public class User implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@NotNull
    private String name;
    @NotNull
	private String pass;//when persisted it'll be encrypted
    
    @Id
    @NotNull
    @Pattern(regexp = ".+@.+\\..+")
    private String email;
    
    @Nullable
    private String profilePictureUrl;
    
    private boolean isVerified;
    //TODO: FK
    @OneToMany
    private List<Post> posts;
    //TODO: FK
    @OneToMany
    private List<Comment> comments;
    
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private UserEmailToken userToken;
    private UUID userIdentifier;
    
    public User() {}
    
    public User(String name, String email, String pass) {
    	this.name = name;
    	this.email = email;
    	this.pass = pass;
    	this.userIdentifier = UUID.randomUUID();
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isVerified() {
		return isVerified;
	}
	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public UserEmailToken getUserToken() {
		return userToken;
	}

	public void setUserToken(UserEmailToken userToken) {
		this.userToken = userToken;
	}

	public UUID getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(UUID userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof User)) {
			return false;
		}
		User other = (User)obj;
		return name.equals(other.getName()) && pass.equals(other.getPass()) && email.equals(other.getEmail()) && isVerified == other.isVerified() && userIdentifier.equals(other.getUserIdentifier());
	}
	@Override
	public int hashCode() {
		int prime = 13;
		int res = 9;
		res = res*prime + name.hashCode();
		res = res*prime + pass.hashCode();
		res = res*prime + email.hashCode();
		res = res*prime + (isVerified? 1 : 0);
		res = res*prime + userIdentifier.hashCode();
		return res;
	}
	@Override
	public String toString() {
		return "USER: { name: "+name+", e-mail: "+email+", is Verified: "+isVerified+" };";
	}
    
    public boolean comparePass(String retrievedEncryptedPass) {
    	StrongPasswordEncryptor passwordCompare = new StrongPasswordEncryptor();
    	return passwordCompare.checkPassword(this.pass, retrievedEncryptedPass);
    }
    
}
