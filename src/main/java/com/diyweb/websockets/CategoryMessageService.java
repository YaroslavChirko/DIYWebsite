package com.diyweb.websockets;

import java.io.IOException;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket/posts/{category}")
public class CategoryMessageService{
	
	private static Session session;
	
	@OnOpen
	public void openConnection(Session passedSession, @PathParam("category") String category) {
		session = passedSession;
		System.out.println("session opened: "+session.getPathParameters().get("category"));
	}
	
	@OnClose
	public void closeConnection(Session session) {
		System.out.println("session closed: "+session.getPathParameters().get("category"));
		session = null;
	}
	
	public static void sendUpdateAck(@PathParam("category") String category) {
		
		if(session != null) {
			for(Session clientSession : session.getOpenSessions()) {
				if(clientSession.getPathParameters().get("category").equals(category)){
					try {
						clientSession.getBasicRemote().sendText("New post was added to "+category);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
