package com.dev.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dev.reader.property.PropertyReader;
import com.dev.reader.property.constants.PropertyConstants;
import com.dev.util.LabelsAndMessagesUtil;
import com.dev.util.MailUtil;

/**
 * 
 * @author Sany Jones R
 * @since 1.0
 */
public class LabelMessageValidator
{
	public static Map<String, String> propertyValues = new HashMap<String, String>();

	static String downloadFolderLocation = null;

	public static String labelsCheckoutLocation = null;

	public static String messagesCheckoutLocation = null;

	public static String scriptCheckoutLocation = null;

	static File rootFolder = null;

	public static Map<String, List<File>> filesMap = new HashMap<String, List<File>>();

	static Map<String, String> skippedFiles = new HashMap<String, String>();
	
	public static Map<String, HashMap<String, String>> responseMailValues = new HashMap<String, HashMap<String, String>>();

	public static File labelsToUpdate = null;

	public static File messagesToUpdate = null;

	public static File newLabels = null;

	public static File newMessages = null;

	public static long updateLabelCount = 0L;

	public static long updateMessageCount = 0L;

	public static long newLabelCount = 0L;

	public static long newMessageCount = 0L;

	public static long unprocessed = 0L;

	public static long processed = 0L;
	
	public static boolean proceed = false;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HHmm");

	/*public LabelMessageValidator() throws Exception {
		try {
			initialize();
		} catch (Exception e) {
			throw e;
		}
	}*/

	public static void main(String[] args) throws Exception {
		initialize();
		/* SVNUtil.doRevert(); */
		/*
		 * if (LabelMessageValidator.proceed) { LabelMessageValidator.proceed = false;
		 */
		MailUtil mailUtil = new MailUtil();
		mailUtil.readMail();
		if (LabelMessageValidator.proceed) {
			LabelMessageValidator.proceed = true;
			process();
			if (LabelMessageValidator.processed > 0) {
				mailUtil.prepareResponseMail();
				mailUtil.sendResponseMail();
			}
		}
		/* } */
	}

	private static void initialize() {
		try {
			propertyValues.put(PropertyConstants.FILE_NAME, PropertyConstants.CONFIGURATION_PROPERTIES);
			new PropertyReader().getPropertyValues(propertyValues);
			labelsCheckoutLocation = propertyValues.get(PropertyConstants.CHECKOUT_LOCATION) + File.separator + "ui" + File.separator + "js" + File.separator + "core" + File.separator + "common" + File.separator + "labels_en.js";
			messagesCheckoutLocation = propertyValues.get(PropertyConstants.CHECKOUT_LOCATION) + File.separator + "ui" + File.separator + "js" + File.separator + "core" + File.separator + "common" + File.separator + "messages_en.js";
			scriptCheckoutLocation = propertyValues.get(PropertyConstants.CHECKOUT_LOCATION);
			downloadFolderLocation = propertyValues.get(PropertyConstants.BASE_PATH) + File.separator + "downloadFiles";
			rootFolder = new File(propertyValues.get(PropertyConstants.BASE_PATH));
			labelsToUpdate = new File(downloadFolderLocation + File.separator + "labelsToUpdate");
			messagesToUpdate = new File(downloadFolderLocation + File.separator + "messagesToUpdate");
			newLabels = new File(downloadFolderLocation + File.separator + "newLabels");
			newMessages = new File(downloadFolderLocation + File.separator + "newMessages");
			if (!rootFolder.exists()) {
				rootFolder.mkdir();
			}
			rootFolder = new File(downloadFolderLocation);
			if (!rootFolder.exists()) {
				rootFolder.mkdir();
				labelsToUpdate.mkdir();
				messagesToUpdate.mkdir();
				newLabels.mkdir();
				newMessages.mkdir();
			}
		} catch (Exception e) {

		}
	}

	private static void process() {
		try {
			LabelsAndMessagesUtil labelsAndMessagesUtil = new LabelsAndMessagesUtil();
			for (Map.Entry<String, List<File>> entry : filesMap.entrySet()) {
				if (entry.getKey().equals("UPDATE_LABELS")) {
					System.out.println("*******Updating Labels in-progess*****");
					for (File downloadFile : entry.getValue()) {
						System.out.println("File: '" + downloadFile.getName() + "' processing....");
						labelsAndMessagesUtil.responseValues = responseMailValues.get(downloadFile.getName().split("-")[1].trim().split(".txt")[0]);
						labelsAndMessagesUtil.updateLabelsFile = downloadFile;
						labelsAndMessagesUtil.updateLabelsOrMessagesInJs(true);
					}
				} else if (entry.getKey().equals("UPDATE_MESSAGES")) {
					System.out.println("*******Updating Messages in-progess*****");
					for (File downloadFile : entry.getValue()) {
						System.out.println("File: '" + downloadFile.getName() + "' processing....");
						labelsAndMessagesUtil.responseValues = responseMailValues.get(downloadFile.getName().split("-")[1].trim().split(".txt")[0]);
						labelsAndMessagesUtil.updateMessagesFile = downloadFile;
						labelsAndMessagesUtil.updateLabelsOrMessagesInJs(false);
					}
				} else if (entry.getKey().equals("NEW_LABELS")) {
					System.out.println("*******New Labels in-progess*****");
					for (File downloadFile : entry.getValue()) {
						System.out.println("File: '" + downloadFile.getName() + "' processing....");
						labelsAndMessagesUtil.responseValues = responseMailValues.get(downloadFile.getName().split("-")[1].trim().split(".txt")[0]);
						labelsAndMessagesUtil.createLabelsFile = downloadFile;
						labelsAndMessagesUtil.addLabelsOrMessagesInJs(true);
					}
				} else if (entry.getKey().equals("NEW_MESSAGES")) {
					System.out.println("*******New Messages in-progess*****");
					for (File downloadFile : entry.getValue()) {
						System.out.println("File: '" + downloadFile.getName() + "' processing....");
						labelsAndMessagesUtil.responseValues = responseMailValues.get(downloadFile.getName().split("-")[1].trim().split(".txt")[0]);
						labelsAndMessagesUtil.createMessagesFile = downloadFile;
						labelsAndMessagesUtil.addLabelsOrMessagesInJs(false);
					}
				}
			}
			labelsAndMessagesUtil.process();
			rootFolder.renameTo(new File(downloadFolderLocation + "_" + sdf.format(new Date())));
			System.out.println("\n\n*******Final Consolidated Report*****\n");
			System.out.println("Updated Labels Count: " + LabelMessageValidator.updateLabelCount);
			System.out.println("Updated Messages Count: " + LabelMessageValidator.updateMessageCount);
			System.out.println("New Labels Count: " + LabelMessageValidator.newLabelCount);
			System.out.println("New Messages Count: " + LabelMessageValidator.newMessageCount);
			System.out.println("Unprocessed key value Count: " + LabelMessageValidator.unprocessed + "\n");
			System.out.println("Labels and Messages processing completed\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateFiles(){
		
	}
}
