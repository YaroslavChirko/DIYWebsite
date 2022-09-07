package com.diyweb.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * This class is used to assign user a token for e-mail verification<br/> 
 * or when password reset is needed.<br/>
 * It should be reset after some time passed (say 20 minutes) or when it was used for the first time.
 * @author erick
 *
 */
@Entity
@Table(name = "DiyUserEmailToken")
public class UserEmailToken {
	
	@Id
	private UUID token;
	private LocalDateTime createdAt;
	
	@OneToOne
	@JoinColumn(name="owner_email", referencedColumnName="email")
	private User owner;
	
	@Transient
	public static int timeTillInvalidation = 20;//in minutes
	
	public UserEmailToken() {}
	
	public UserEmailToken(User user) {
		this.owner = user;
		this.token = UUID.randomUUID();
		this.createdAt = LocalDateTime.now();
	}

	public UUID getToken() {
		return token;
	}

	public void setToken(UUID token) {
		this.token = token;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof UserEmailToken)) {
			return false;
		}
		
		UserEmailToken other = (UserEmailToken)obj;
		return token.equals(other.getToken()) && createdAt.equals(other.getCreatedAt()) && owner.equals(other.getOwner());
	}

	@Override
	public int hashCode() {
		int prime = 23;
		int res = 15;
		
		res = res*prime + token.hashCode();
		res = res*prime + createdAt.hashCode();
		res = res*prime + owner.hashCode();
		
		return res;
	}

	@Override
	public String toString() {
		return "TOKEN: { Owner: "+owner.getName()+" at "+owner.getEmail()+",\nCreated at "
				+createdAt+", invalidates at "+createdAt.plusMinutes(timeTillInvalidation)+
				"\nToken: "+token+"}";
	}
	
	
	
}
