package com.corejsf;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Named
@SessionScoped
public class NewAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String emailAddress;
	private String password;
	
	@Resource(name="mail/corejsf")
	private Session mailSession;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String create() {
		try {
			createAccount();
			sendNotification();
			
			return "done";
		} catch (Exception e) {
			Logger.getLogger("com.corejsf").log(Level.SEVERE, "login failed", e);
			
			return "error";
		}
	}
	
	private void createAccount() {
		int BASE = 36;
		int LENGTH = 8;
		
		password = Long.toString((long)(Math.pow(BASE, LENGTH) * Math.random()), BASE);
	}
	
	private void sendNotification() throws MessagingException {
		ResourceBundle bundle = ResourceBundle.getBundle("com.corejsf.messages");
		
		String subject = bundle.getString("subject");
		String body = bundle.getString("body");
		String messageText = MessageFormat.format(body, name, password);
		
		mailSession.setDebug(true);
		MimeMessage message = new MimeMessage(mailSession);
		
		Address toAddress = new InternetAddress(emailAddress);
		message.setRecipient(RecipientType.TO, toAddress);
		message.setSubject(subject);
		message.setText(messageText);
		message.saveChanges();
		
		Transport tr = mailSession.getTransport();
		tr.connect(null, null);
		tr.sendMessage(message, message.getAllRecipients());
		
		tr.close();
	}
}
