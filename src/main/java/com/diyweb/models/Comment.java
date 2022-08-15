package com.diyweb.models;

import java.time.LocalDateTime;

import internal.com.sun.istack.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *This class is used as a model for user comments.<br/>
 *It contains all the information needed for proper representation.<br/>
 *As a table record it'll contain references to {@code User} who posted it,<br/>
 *{@code Post} it was written for and optional {@code Comment} if it's a reply 
 * @author erick
 */

@Entity
@Table(name = "DiyComment")
public class Comment {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private int id;
	
	//TODO: FK
	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    //TODO: limit to some 3k symbols or something
	@Size(max = 3000)
	@NotNull
    private String body;
    
    //TODO: FK
	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
    private Post origin;
    private LocalDateTime postedAt;
    
    @ManyToOne
    @Nullable
    private Comment replyingTo;//can be null

    public Comment() {}
    
    public Comment(User author, String body, Post origin, Comment replyingTo) {
    	this.author = author;
    	this.body = body;
    	this.origin = origin;
    	this.postedAt = LocalDateTime.now();
    	this.replyingTo = replyingTo;
    }
    
    public Comment(User author, String body, Post origin) {
    	this(author, body, origin, null);
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Post getOrigin() {
		return origin;
	}

	public void setOrigin(Post origin) {
		this.origin = origin;
	}

	public LocalDateTime getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(LocalDateTime postedAt) {
		this.postedAt = postedAt;
	}

	public Comment getReplyingTo() {
		return replyingTo;
	}

	public void setReplyingTo(Comment replyingTo) {
		this.replyingTo = replyingTo;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Comment)) {
			return false;
		}
		
		Comment other = (Comment)obj;
		
		return author.equals(other.getAuthor()) 
				&& body.equals(other.getBody()) 
				&& origin.equals(other.getOrigin()) 
				&& postedAt.equals(other.getPostedAt()) 
				&& replyingTo.equals(other.getReplyingTo());
	}

	@Override
	public int hashCode() {
		int prime = 13;
		int res = 17;
		res = res*prime + author.hashCode();
		res = res*prime + body.hashCode();
		res = res*prime + origin.hashCode();
		res = res*prime + postedAt.hashCode();
		res = res*prime + replyingTo.hashCode();
		return res;
	}

	@Override
	public String toString() {
		return "COMMENT: { Author\'s name: "+author.getName()+",\nCropped body"+body.substring(0, 100)+
				"... ,/nFrom post with title"+origin.getTitle()+", posted at: "
				+postedAt+", Replying to: "+replyingTo.author.getName()+" };";
	}
    
    
}
