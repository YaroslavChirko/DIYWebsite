package com.diyweb.repo;

import java.util.List;

import com.diyweb.models.Comment;
import com.diyweb.models.Post;

public interface CommentRepoInterface {
	public void persist(Comment comment);
	public List<Comment> getCommentsByPost(Post post);
	public Comment getCommentById(int id);
	public boolean updateComment(Comment comment);
	public void addReply(Comment reply, Comment toReply);
	
}
