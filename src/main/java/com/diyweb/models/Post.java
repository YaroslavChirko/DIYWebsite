
package com.diyweb.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This class is a model for persisting user posts.<br/>
 * It contains references to {@code User} who posted it as well as list of {@code Comment}s to this post.
 * @author erick
 */
@Entity
@Table(name = "DiyPost")
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotNull
    private User author;
	
	@NotNull
    private String title;
    
    @Enumerated
    @NotNull
    private Cathegory cathegory;
    
    @Size(max = 30000)
    private String body;
    
    private List<String> pictureUrls; //paths to pictures used in the body, perhaps make field for tumbnail too
    private LocalDateTime postedAt;
    
    //TODO: FK
    @OneToMany
    @JoinColumn(name = "comment_id", unique = true)
    private List<Comment> comments;

    public Post() {}
    
    public Post(User author, String title, Cathegory cathegory, String body, List<String> pictureUrls) {
    	this.author = author;
    	this.title = title;
    	this.cathegory = cathegory;
    	this.body = body;
    	this.pictureUrls = pictureUrls;
    	this.postedAt = LocalDateTime.now();
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public Cathegory getCathegory() {
		return cathegory;
	}

	public void setCathegory(Cathegory cathegory) {
		this.cathegory = cathegory;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<String> getPictureUrls() {
		return pictureUrls;
	}

	public void setPictureUrls(List<String> pictureUrls) {
		this.pictureUrls = pictureUrls;
	}

	public LocalDateTime getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(LocalDateTime postedAt) {
		this.postedAt = postedAt;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Post)) {
			return false;
		}
		Post other = (Post)obj;
		
		return author.equals(other.author) && title.equals(other.getTitle()) && cathegory.equals(other.getCathegory()) && body.equals(other.getBody()) && postedAt.equals(other.getPostedAt());
	}

	@Override
	public int hashCode() {
		int prime = 23;
		int res = 15;
		res = res*prime + author.hashCode();
		res = res*prime + title.hashCode();
		res = res*prime + cathegory.hashCode();
		res = res*prime + body.hashCode();
		res = res*prime + postedAt.hashCode();
		return res;
	}

	@Override
	public String toString() {
		return "POST: { Title: "+title+", Author\'s name: "+author.getName()+",\nCropped body: "
				+body.substring(0, 100)+"... , Posted at: "+postedAt+" };";
	}
    
    
}
