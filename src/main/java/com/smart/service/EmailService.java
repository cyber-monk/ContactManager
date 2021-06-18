package com.smart.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class EmailService {

		//This method is responsible to send email [Without Attachment]
		public boolean sendEmail(String message, String subject, String to) {
			boolean result = false;
			String from = "cybermonk24@gmail.com";
			String fromPassword = "Pass@123";
			
			// Variable for gmail host.
			String host = "smtp.gmail.com";
			
			//Get system properties.
			Properties properties = System.getProperties();
			
			//Setting important information to properties object		
			//Host set
			properties.put("mail.smtp.host",host);
			properties.put("mail.smtp.port","465");
			properties.put("mail.smtp.ssl.enable","true");
			properties.put("mail.smtp.auth","true");
			
			//Step 1: To Get Session object..
			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, fromPassword);
				}
			});
			
			//session.setDebug(true);
				
			//Step 2: Compose the message [Text, multimedia]
			MimeMessage mimeMessage = new MimeMessage(session);
			
			try {
				//From
				mimeMessage.setFrom(from);
				
				//TO - Adding receipent
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				
				//Adding Subject message
				mimeMessage.setSubject(subject);
				
				//Adding text to message
				//mimeMessage.setText(message);
				mimeMessage.setContent(message,"text/html");
				
			//Step 3: Send message using trnsport class
				Transport.send(mimeMessage);
				
				System.out.println("Mail send successfully...");
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				return result;
			}	
			
			return result;
		}
	
	
	
	
	
	
}
