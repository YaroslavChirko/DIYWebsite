package com.diyweb.websockets;

import java.io.IOException;
import java.util.Map;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/posts/read/{category}/{postId}")
public class CommentSocketService {
	
	private static  Session session;
	
	@OnOpen
	public void openConnection(Session passedSession, @PathParam("postId") String postId) {
		System.out.println("Connected to: "+postId+" comment endpoint");
		session = passedSession;
	}
	
	@OnClose
	public void closeConnection(@PathParam("postId") String postId) {
		System.out.println("Closing connection to: "+postId+" comment endpoint");
	}
	
	public static void sendCommentMessage(@PathParam("category") String category, @PathParam("postId") String postId) {
		if(session != null) {
			for(Session clientSession: session.getOpenSessions()) {
				Map<String, String> clientSessionParam = clientSession.getPathParameters();
				//if(clientSessionParam.get("postId").equals(postId) && clientSessionParam.get("category").equals(category)) {
					System.out.println("params are present");
					try {
						clientSession.getBasicRemote().sendText("category:"+category+", postId:"+postId);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				//}
			}
		}
	}
}
