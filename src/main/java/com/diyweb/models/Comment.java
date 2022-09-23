package com.diyweb.models;

import java.time.LocalDateTime;
import java.util.Set;

import internal.com.sun.istack.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_id", nullable = false)
    private Post origin;
	
    private LocalDateTime postedAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @Nullable
    private Comment replyingTo;//can be null
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "replyingTo")
	private Set<Comment> replies;

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
		res = res*prime + (replyingTo==null?0:replyingTo.hashCode());
		return res;
	}

	@Override
	public String toString() {
		String str = "COMMENT: { Author\'s name: "+author.getName()+",\nCropped body"+body.substring(0, Math.min(body.length(), 100))+
				"... ,/nFrom post with title"+origin.getTitle()+", posted at: "
				+postedAt;
		if(replyingTo != null) {
			str += ", Replying to: "+replyingTo.author.getName();
		}
		
		str += " };";
		return str;
	}
    
	public String returnHtmlTree() {
		StringBuilder resultBuilder = new StringBuilder();
		
		resultBuilder.append("<div id=\"comment-holder\">");
		resultBuilder.append("<h4>"+this.getAuthor().getEmail()+"</h4>");
		resultBuilder.append("<h5>"+this.getPostedAt()+"</h5>");
		resultBuilder.append("<p>"+this.getBody()+"</p>");
		resultBuilder.append("<form action=\"/DIYWebsite/comment/add/"+this.getOrigin().getCathegory()+"/"+this.getOrigin().getId()+"\" method=\"post\" class=\"col-2\" id=\"reply-form-"+this.getId()+"\">");
		resultBuilder.append("<input type=\"hidden\" name=\"to-reply\" value=\""+this.getId()+"\" />");
		resultBuilder.append("<button class=\"btn btn-link reply-form-btn\" type=\"button\" onclick=\"addReplyForm("+this.getId()+")\" id=\"reply-form-button-"+this.getId()+"\">Add Reply</button>");
		resultBuilder.append("</form>");
		resultBuilder.append("<form action=\"/DIYWebsite/comment/delete/"+this.getOrigin().getCathegory()+"/"+this.getOrigin().getId()+"/"+this.getId()+"\" method=\"post\" class=\"col-2\">");
		resultBuilder.append("<button class=\"btn btn-danger\">Delete Comment</button>");
		resultBuilder.append("</form>");
		resultBuilder.append("</div>");
		
		for(Comment reply: this.replies) {
			resultBuilder.append("<div style=\"margin-left:40px;\">");
			resultBuilder.append(reply.returnHtmlTree());
			resultBuilder.append("</div>");
		}
		
		
		return resultBuilder.toString();
	}
    
}
