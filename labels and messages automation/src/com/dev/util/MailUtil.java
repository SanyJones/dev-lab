package com.dev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import com.dev.main.LabelMessageValidator;
import com.dev.reader.property.constants.PropertyConstants;

/**
 *
 * @author Sany Jones R
 * @since 1.0
 */
public class MailUtil
{

	Properties mailProperties = null;

	public static Map<String, HashMap<String, Object>> responseMail = new HashMap<String, HashMap<String, Object>>();

	public void readMail() throws Exception {
		try {
			initializeMailProperties(true);
			Session emailSession = Session.getDefaultInstance(mailProperties);
			Store store = emailSession.getStore(PropertyConstants.STORE_NAME);
			try {
				store.connect(LabelMessageValidator.propertyValues.get(PropertyConstants.PROTOCOL), LabelMessageValidator.propertyValues.get(PropertyConstants.USER_NAME), LabelMessageValidator.propertyValues.get(PropertyConstants.PASSWORD));
				LabelMessageValidator.proceed = true;
			} catch (AuthenticationFailedException afe){
				System.out.println("Invalid mail credentials, kindly check your credentials in 'configuration.properties' and try again.");
			}
			if (LabelMessageValidator.proceed) {
				Folder inbox = store.getFolder("INBOX");
				inbox.open(Folder.READ_ONLY);
				Message[] mails = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
				for (int i = 0, n = mails.length; i < n; i++) {
					Message message = mails[i];
					if (message.getContentType().contains("multipart")) {
						message.getContent();
						/*
						 * Address[] AddressArray = message.getFrom(); String[] fromAddressArray = AddressArray[0].toString().split("<.*>"); responseMail.put(fromAddressArray[0], new String("Hi\n"));
						 */
						Multipart multipart = (Multipart) message.getContent();
						String downloadFileLocation = null;
						for (int j = 0; j < multipart.getCount(); j++) {
							MimeBodyPart mimeBodyPart = (MimeBodyPart) multipart.getBodyPart(j);
							if (Part.ATTACHMENT.equalsIgnoreCase(mimeBodyPart.getDisposition())) {
								String fileName = mimeBodyPart.getFileName();
								if (fileName != null && (fileName.contains("updateLabels") || fileName.contains("updateMessages") || fileName.contains("newLabels") || fileName.contains("newMessages"))) {
									initializeResponseMail(message, mimeBodyPart.getFileName().split("-")[1].trim());
									if (fileName.contains("updateLabels")) {
										if (!LabelMessageValidator.filesMap.containsKey("UPDATE_LABELS")) {
											LabelMessageValidator.filesMap.put("UPDATE_LABELS", new ArrayList<File>());
										}
										downloadFileLocation = LabelMessageValidator.labelsToUpdate.getPath() + File.separator.concat(mimeBodyPart.getFileName());
										mimeBodyPart.saveFile(downloadFileLocation);
										LabelMessageValidator.filesMap.get("UPDATE_LABELS").add(new File(downloadFileLocation));
									} else if (fileName.contains("updateMessages")) {
										if (!LabelMessageValidator.filesMap.containsKey("UPDATE_MESSAGES")) {
											LabelMessageValidator.filesMap.put("UPDATE_MESSAGES", new ArrayList<File>());
										}
										downloadFileLocation = LabelMessageValidator.messagesToUpdate.getPath() + File.separator.concat(mimeBodyPart.getFileName());
										mimeBodyPart.saveFile(downloadFileLocation);
										LabelMessageValidator.filesMap.get("UPDATE_MESSAGES").add(new File(downloadFileLocation));
									} else if (fileName.contains("newLabels")) {
										if (!LabelMessageValidator.filesMap.containsKey("NEW_LABELS")) {
											LabelMessageValidator.filesMap.put("NEW_LABELS", new ArrayList<File>());
										}
										downloadFileLocation = LabelMessageValidator.newLabels.getPath() + File.separator.concat(mimeBodyPart.getFileName());
										mimeBodyPart.saveFile(downloadFileLocation);
										LabelMessageValidator.filesMap.get("NEW_LABELS").add(new File(downloadFileLocation));
									} else if (fileName.contains("newMessages")) {
										if (!LabelMessageValidator.filesMap.containsKey("NEW_MESSAGES")) {
											LabelMessageValidator.filesMap.put("NEW_MESSAGES", new ArrayList<File>());
										}
										downloadFileLocation = LabelMessageValidator.newMessages.getPath() + File.separator.concat(mimeBodyPart.getFileName());
										mimeBodyPart.saveFile(downloadFileLocation);
										LabelMessageValidator.filesMap.get("NEW_MESSAGES").add(new File(downloadFileLocation));
									}
								}
							}
						}
					}
				}
				inbox.close(false);
				store.close();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void initializeMailProperties(boolean read) {
		try {
			mailProperties = new Properties();
			if (read) {
				mailProperties.put("mail.store.protocol", LabelMessageValidator.propertyValues.get(PropertyConstants.PROTOCOL));
			} else {
				mailProperties.put("mail.smtp.host", LabelMessageValidator.propertyValues.get(PropertyConstants.PROTOCOL));
				mailProperties.put("mail.smtp.socketFactory.port", LabelMessageValidator.propertyValues.get(PropertyConstants.PORT));
				mailProperties.put("mail.smtp.socketFactory.class", LabelMessageValidator.propertyValues.get(PropertyConstants.SOCKET_FACTORY));
				mailProperties.put("mail.smtp.auth", LabelMessageValidator.propertyValues.get(PropertyConstants.AUTH));
				mailProperties.put("mail.smtp.port", LabelMessageValidator.propertyValues.get(PropertyConstants.PORT));
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void initializeResponseMail(Message message, String requestorName) {
		try {
			if (requestorName != null) {
				requestorName = requestorName.split(".txt")[0];
				if (!LabelMessageValidator.responseMailValues.containsKey(requestorName)) {
					responseMail.put(requestorName, new HashMap<String, Object>());
					LabelMessageValidator.responseMailValues.put(requestorName, new HashMap<String, String>());
				}
				responseMail.get(requestorName).put("TO", message.getRecipients(RecipientType.TO));
				responseMail.get(requestorName).put("CC", message.getRecipients(RecipientType.CC));
				HashMap<String, String> responseValues = new HashMap<String, String>();
				responseValues.put("requestorName", requestorName);
				responseValues.put("updateLabelCount", "0");
				responseValues.put("updateMessageCount", "0");
				responseValues.put("newLabelCount", "0");
				responseValues.put("newMessageCount", "0");
				responseValues.put("processed", "0");
				responseValues.put("unprocessedCount", "0");
				responseValues.put("errorMessage", "");
				LabelMessageValidator.responseMailValues.put(requestorName, responseValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void prepareResponseMail() {
		try {
			for (Map.Entry<String, HashMap<String, String>> entry : LabelMessageValidator.responseMailValues.entrySet()) {
				Long processed = Long.valueOf(entry.getValue().get("processed"));
				String responseContent = "";
				if (processed > 0L) {
					responseContent = LabelMessageValidator.propertyValues.get(PropertyConstants.RESPONSE_MAIL_CONTENT);
					// System.out.println("\n\nResponse mail for **" + entry.getValue().get("requestorName") + "**\n");
					responseContent = responseContent.replace("$requestorName", entry.getValue().get("requestorName"));
					responseContent = responseContent.replace("$updateLabelCount", entry.getValue().get("updateLabelCount"));
					responseContent = responseContent.replace("$updateMessageCount", entry.getValue().get("updateMessageCount"));
					responseContent = responseContent.replace("$newLabelCount", entry.getValue().get("newLabelCount"));
					responseContent = responseContent.replace("$newMessageCount", entry.getValue().get("newMessageCount"));
					responseContent = responseContent.replace("$revisonNumber", LabelsAndMessagesUtil.commitVersion.toString());
					Long unprocessedCount = Long.valueOf(entry.getValue().get("unprocessedCount"));
					if (unprocessedCount > 0L) {
						responseContent.concat("<p style='color: red; font-weight: bold;'>Error:</p>");
						//String errorMessage = "\nReason for unprocessed count:\n" + entry.getValue().get("errorMessage");
						responseContent = responseContent.replace("$errorMessage", entry.getValue().get("errorMessage"));
					} else {
						responseContent = responseContent.replace("$errorMessage", "");
					}
					responseContent = responseContent.replace("$unprocessedCount", unprocessedCount.toString());
					// System.out.println(responseContent);
				} else {
					responseContent = LabelMessageValidator.propertyValues.get(PropertyConstants.FAILURE_RESPONSE_MAIL_CONTENT);
					responseContent = responseContent.replace("$requestorName", entry.getValue().get("requestorName"));
					Long unprocessedCount = Long.valueOf(entry.getValue().get("unprocessedCount"));
					if (unprocessedCount > 0L) {
						String errorMessage = "<p>Sorry your request can't be processed. Please find the reason below.</p>" + entry.getValue().get("errorMessage");
						responseContent = responseContent.replace("$errorMessage", errorMessage);
					} else {
						String errorMessage = "<p>Sorry your request can't be processed. Kindly make sure that your file content is in proper format.</p>";
						responseContent = responseContent.replace("$errorMessage", errorMessage);
					}
					// System.out.println(responseContent);
				}
				responseMail.get(entry.getKey()).put("responseContent", responseContent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendResponseMail() {
		try {
			/*for (Map.Entry<String, HashMap<String, Object>> entry : responseMail.entrySet()) {
				System.out.println("\n\nPreview of response mail to be sent:\n");
				//System.out.println("**" + entry.getKey() + "**\n");
				System.out.println(entry.getValue().get("responseContent"));
			}*/
			System.out.print("Enter y/n to send response mail:");
			String sendMail = new BufferedReader(new InputStreamReader(System.in)).readLine();
			if (sendMail.contains("y")) {
				initializeMailProperties(false);
				Session session = Session.getInstance (mailProperties, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(LabelMessageValidator.propertyValues.get(PropertyConstants.USER_NAME), LabelMessageValidator.propertyValues.get(PropertyConstants.PASSWORD));
					}
				});

				// compose message
				try {
					for (Map.Entry<String, HashMap<String, Object>> entry : responseMail.entrySet()) {
						MimeMessage message = new MimeMessage(session);
						message.addRecipients(Message.RecipientType.TO, (Address[]) entry.getValue().get("TO"));
						message.addRecipients(Message.RecipientType.CC, (Address[]) entry.getValue().get("CC"));
						message.setSubject(LabelMessageValidator.propertyValues.get(PropertyConstants.RESPONSE_MAIL_SUBJECT));
						message.setContent((String) entry.getValue().get("responseContent"),"text/html" );  
						//message.setText((String) entry.getValue().get("responseContent"));
						// send message
						Transport.send(message);
					}
					System.out.println("Response sent successfully");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
