package com.knpc.dims.filenet.util;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.knpc.dims.filenet.beans.EmailBean;

public class EmailHelper {

	//static Logger logger = Logger.getLogger(EmailHelper.class);
	
	public void postMail(EmailBean emailBean, List<InputStream> ltInputStream, List<String> ltDocNames) {
		boolean debug = false;
		Properties p = new Properties();
		try {
		
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
			p.load(is);
			String SMTP_HOST_NAME = p.getProperty("SMTP_HOST_NAME");
			String SMTP_PORT = p.getProperty("SMTP_PORT");
			String from = p.getProperty("FROM_ADDRESS");
			//logger.debug("Send Mail. SMTP - " + SMTP_HOST_NAME + ", Port - " + SMTP_PORT);
			// Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.port", SMTP_PORT);
			
			//logger.debug("Message " + message);
			Authenticator auth = new SMTPAuthenticator();
			
			Session session = Session.getDefaultInstance(props, auth);
	
			session.setDebug(debug);
			Message msg = new MimeMessage(session);
	
			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(emailBean.getFrom());
			msg.setFrom(addressFrom);
			InternetAddress[] iaTo =null;
			InternetAddress[] iaCc = null;
			InternetAddress[] iaBcc = null;
			for (int i = 0; i < emailBean.getEmailRecipientList().size(); i++) {
				if(emailBean.getEmailRecipientList().get(i).getEmailRecipientType().equalsIgnoreCase("to")){
					iaTo = InternetAddress.parse(emailBean.getEmailRecipientList().get(i).getEmailRecipient());
				}else if(emailBean.getEmailRecipientList().get(i).getEmailRecipientType().equalsIgnoreCase("cc")){
					iaCc = InternetAddress.parse(emailBean.getEmailRecipientList().get(i).getEmailRecipient());
				}else if(emailBean.getEmailRecipientList().get(i).getEmailRecipientType().equalsIgnoreCase("bcc")){
					iaBcc = InternetAddress.parse(emailBean.getEmailRecipientList().get(i).getEmailRecipient());
				}
			}
			if(iaTo!=null){
				msg.setRecipients(Message.RecipientType.TO, iaTo);
			}
			if(iaCc!=null){
				msg.setRecipients(Message.RecipientType.CC, iaCc);
			}
			if(iaBcc!=null){
				msg.setRecipients(Message.RecipientType.BCC, iaBcc);
			}
			// Setting the Subject and Content Type
			msg.setSubject(emailBean.getSubject());
			msg.setText(emailBean.getBody());
			Multipart multipart = new MimeMultipart();
			if(ltInputStream != null && ltDocNames != null){

				if(ltInputStream.size() > 0 && ltDocNames.size() > 0) {

					for (int i = 0; i < ltInputStream.size(); i++) {
						InputStream inputStream = ltInputStream.get(i);
								
						String readStr = "";
						int docLen = 1024;
						byte[] buf = new byte[docLen];
						int n = 1;
						while (n > 0)
						{
							n =  inputStream.read(buf, 0, docLen);
							readStr = readStr + new String(buf);
							buf = new byte[docLen];
						}

						MimeBodyPart messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(readStr);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(ltDocNames.get(i));
						multipart.addBodyPart(messageBodyPart);
						msg.setContent(multipart);

						multipart.addBodyPart(messageBodyPart);
						msg.setContent(multipart);
						inputStream.close();
					}
				}
			}

			Transport.send(msg);
			//logger.debug("Sent Email");
		} catch(Exception e) {
			//logger.error("Exception in Send Mail " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		public PasswordAuthentication getPasswordAuthentication() {
			Properties p = new Properties();
			try {
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DIMS.properties");
				p.load(is);
			} catch(Exception e) {
				//logger.debug(e.getMessage(), e);
			}
			
			String username = p.getProperty("SMTP_AUTH_USER");
			String password = p.getProperty("SMTP_AUTH_PWD");
			//logger.debug("User - " + username + " PWD - " + password);
			return new PasswordAuthentication(username, password);
		}
	}
	
}
