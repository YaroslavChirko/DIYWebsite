package com.diyweb.misc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.diyweb.models.User;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;

/**
 * Class provides static methods for e-mail message composition and sending
 * @author erick
 *
 */
public class MailUtils {
	private static String propertyPath = Thread.currentThread().getContextClassLoader().getResource("mail/").getPath();
	
	/*@Resource(name = "java:app/mail/diyWebSession")
	private static Session session;*/
	private static String[] subjectTextArr = {"E-mail address verification", "Password reset"};
	private static String[] messageTextFileNames = {"message_text.txt", "password_reset_text.txt"};
	/**
	 * @param session
	 * @param sender
	 * @param reciever
	 * @param toVerify
	 * @param hostUrl
	 * @return
	 * @throws MessagingException
	 * @throws IOException 
	 * 
	 * 
	 */
	private static MimeMessage composeMessage(Session session, String sender, String reciever, User toVerify, String hostUrl, EmailType type) throws MessagingException, IOException {
		String subjectText = subjectTextArr[type.id];
		String messageTextFileName = messageTextFileNames[type.id];
		StringBuilder messageTextBuilder = new StringBuilder();
		BufferedReader messageTextStream = new BufferedReader(new FileReader(propertyPath+"/"+messageTextFileName));
		
		String line = null;
		
		while((line = messageTextStream.readLine()) != null ) {
			messageTextBuilder.append(line);
			messageTextBuilder.append("\n");
		}
		
		String messageTextStr = messageTextBuilder.toString();
		messageTextStr = messageTextStr.replaceAll("\\[URL\\]", hostUrl);
		messageTextStr = messageTextStr.replaceAll("\\[Name\\]", toVerify.getName());
		messageTextStr = messageTextStr.replaceAll("\\[UserEmail\\]", toVerify.getEmail());
		messageTextStr = messageTextStr.replaceAll("\\[UserToken\\]", toVerify.getUserToken().getToken().toString());
		
		return composeMessage(session, sender, reciever, toVerify, messageTextStr, subjectText);
	}
	
	/**
	 * @param session
	 * @param sender
	 * @param reciever
	 * @param toVerify
	 * @param hostUrl
	 * @param messageText
	 * @param subjectText
	 * @return
	 * @throws MessagingException
	 * 
	 * Use if need to compose message and provide your own message body and subject
	 */
	private static MimeMessage composeMessage(Session session, String sender, String reciever, User toVerify, String messageText, String subjectText) throws MessagingException {
		MimeMessage message = new MimeMessage(session);
		
		message.setSender(new InternetAddress(sender));
		message.addRecipient(RecipientType.TO, new InternetAddress(reciever));
		message.setSubject(subjectText);
		message.setText(messageText);
		
		return message;
	}
	
	/**
	 * @param message
	 * @param propertyName
	 * @param userPropertyName
	 * 
	 * Use this method to send e-mail message, note that 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws MessagingException 
	 */
	public static void sendMessage(String reciever, User toVerify, String hostUrl, String propertyName, String userPropertyName, EmailType type) throws FileNotFoundException, IOException, MessagingException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertyPath+"/"+propertyName));
		
		Properties userProps = new Properties();
		userProps.load(new FileInputStream(propertyPath+"/"+userPropertyName));
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userProps.getProperty("user.login"), userProps.getProperty("user.pass"));
			}
		});
		
		System.out.println("session is null: "+(session == null));
		
		//session.getStore().connect(userProps.getProperty("user.login"), userProps.getProperty("user.pass"));
		
		MimeMessage message = composeMessage(session, userProps.getProperty("user.login"), reciever, toVerify, hostUrl, type);
		
		//session.getTransport("smtp").send(message);
		Transport.send(message);
	}
	
	
	public enum EmailType{
		
		VERIFICATION(0),
		PASSWORD_RESET(1);
		
		int id;
		EmailType(int id){
			this.id = id;
		}
	}
}
