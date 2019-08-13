package com.dev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dev.main.LabelMessageValidator;
import com.dev.reader.property.constants.PropertyConstants;

/**
 * 
 * @author Venkatesh
 * @since 1.0
 */
public class LabelsAndMessagesUtil
{

	private File messagesEnFile = null;

	private File labelsEnFile = null;

	public File updateMessagesFile = null;

	public File updateLabelsFile = null;
	
	public static Long commitVersion = 0L;

	public HashMap<String, String> responseValues = null;

	public File createMessagesFile = null;

	public File createLabelsFile = null;

	private File queryFile = new File(LabelMessageValidator.propertyValues.get(PropertyConstants.SCRIPT_LOCATION));

	Map<String, String> hashMapLabel = new LinkedHashMap<String, String>();

	Map<String, String> inverseHashMapLabel = new HashMap<String, String>();

	Map<String, String> hashMapMessage = new LinkedHashMap<String, String>();

	Map<String, String> inverseHashMapMessage = new HashMap<String, String>();

	StringBuffer allQuery = new StringBuffer();

	public LabelsAndMessagesUtil() {
		try {
			labelsEnFile = new File(LabelMessageValidator.labelsCheckoutLocation);
			messagesEnFile = new File(LabelMessageValidator.messagesCheckoutLocation);
			BufferedReader jsLabelBr = new BufferedReader(new InputStreamReader(new FileInputStream(labelsEnFile)));
			String readSingleLine = "";
			while ((readSingleLine = jsLabelBr.readLine()) != null) {
				List<String> keyAndValue = returnKeyAndValue(readSingleLine);
				if (keyAndValue.size() != 2) {
					continue;
				}
				if (hashMapLabel.get(keyAndValue.get(0)) != null) {
					System.out.println("Label Key is alreday present : " + keyAndValue.get(0));
				}
				if (inverseHashMapLabel.get(keyAndValue.get(1)) != null) {
					// System.out.println("Label Value is alreday present : " + keyAndValue.get(1));
				}
				hashMapLabel.put(keyAndValue.get(0), keyAndValue.get(1));
				inverseHashMapLabel.put(keyAndValue.get(1), keyAndValue.get(0));
			}
			jsLabelBr.close();

			BufferedReader jsMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(messagesEnFile)));
			while ((readSingleLine = jsMessageBr.readLine()) != null) {
				List<String> keyAndValue = returnKeyAndValue(readSingleLine);
				if (keyAndValue.size() != 2) {
					continue;
				}
				if (hashMapMessage.get(keyAndValue.get(0)) != null) {
					System.out.println("Message Key is alreday present : " + keyAndValue.get(0));
				}
				if (inverseHashMapMessage.get(keyAndValue.get(1)) != null) {
					// System.out.println("Message Value is alreday present : " + keyAndValue.get(1));
				}
				hashMapMessage.put(keyAndValue.get(0), keyAndValue.get(1));
				inverseHashMapMessage.put(keyAndValue.get(1), keyAndValue.get(0));
			}
			jsMessageBr.close();

			// allQuery.append("Please check if all key and values are already present in sql files,\n\n");

		} catch (Exception ex) {

		}
	}

	public void updateLabelsOrMessagesInJs(Boolean isLabel) throws IOException {

		String fileName = isLabel ? "Label" : "Message";
		// allQuery.append("Update queries for " + fileName + " : \n\n");
		// System.out.println("\n\n");

		try {
			File updateLabelOrMessage = isLabel ? updateLabelsFile : updateMessagesFile;
			String newFileLine = "";
			BufferedReader updateLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateLabelOrMessage)));

			while ((newFileLine = updateLabelOrMessageBr.readLine()) != null) {
				List<String> keyAndValue = returnKeyAndValue(newFileLine);

				if (keyAndValue.size() != 2) {
					continue;
				}
				// allQuery.append("Key : " + keyAndValue.get(0) + " Value : " + keyAndValue.get(1) + "\n");

				if (isLabel ? inverseHashMapLabel.get(keyAndValue.get(1)) != null : inverseHashMapMessage.get(keyAndValue.get(1)) != null) {
					incrementCounter("unprocessedCount");
					// System.out.println("Failed item");
					String key = isLabel ? inverseHashMapLabel.get(keyAndValue.get(1)) : inverseHashMapMessage.get(keyAndValue.get(1));
					System.out.println(newFileLine + " can't be updated, it's value is present in the " + fileName + " JS file. Use " + key + " : \"" + keyAndValue.get(1) + "\".");
					responseValues.put("errorMessage", responseValues.get("errorMessage").concat("<p>"+newFileLine + " can't be updated, it's value is present in the " + fileName + " JS file. Use " + key + " : \"" + keyAndValue.get(1) + "\".</p>"));
					LabelMessageValidator.unprocessed++;
				} else if (isLabel ? hashMapLabel.get(keyAndValue.get(0)) == null : hashMapMessage.get(keyAndValue.get(0)) == null) {
					// System.out.println("Failed item");
					incrementCounter("unprocessedCount");
					System.out.println(newFileLine + " can't be updated, it's key is not present in the " + fileName + " JS file.");
					responseValues.put("errorMessage", responseValues.get("errorMessage").concat("<p>"+newFileLine + " can't be updated, it's key is not present in the " + fileName + " JS file.</p>"));
					LabelMessageValidator.unprocessed++;
				} else if (keyAndValue.get(0).length() > 0 && keyAndValue.get(1).length() > 0) {
					if (isLabel) {
						incrementCounter("updateLabelCount");
						hashMapLabel.put(keyAndValue.get(0), keyAndValue.get(1));
						inverseHashMapLabel.put(keyAndValue.get(1), keyAndValue.get(0));
						incrementCounter("processed");
						LabelMessageValidator.updateLabelCount++;
						LabelMessageValidator.processed++;
					} else {
						incrementCounter("updateMessageCount");
						hashMapMessage.put(keyAndValue.get(0), keyAndValue.get(1));
						inverseHashMapMessage.put(keyAndValue.get(1), keyAndValue.get(0));
						incrementCounter("processed");
						LabelMessageValidator.updateMessageCount++;
						LabelMessageValidator.processed++;
					}
					allQuery.append("\n" + updateQueryGenerator(isLabel, keyAndValue.get(0), keyAndValue.get(1), "English"));
				}

			}
			updateLabelOrMessageBr.close();
		} catch (IOException ex) {
			throw ex;
		}

	}

	public void addLabelsOrMessagesInJs(Boolean isLabel) throws IOException {

		String fileName = isLabel ? "Label" : "Message";
		// allQuery.append("Insert queries for " + fileName + " : \n\n");
		// System.out.println("\n\n");

		try {

			File createLabelOrMessage = isLabel ? createLabelsFile : createMessagesFile;
			String newFileLine = "";
			BufferedReader createLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(createLabelOrMessage)));

			while ((newFileLine = createLabelOrMessageBr.readLine()) != null) {
				List<String> keyAndValue = returnKeyAndValue(newFileLine);
				if (keyAndValue.size() != 2) {
					continue;
				}

				// allQuery.append("Key : " + keyAndValue.get(0) + " Value : " + keyAndValue.get(1) + "\n");
				if (isLabel ? hashMapLabel.get(keyAndValue.get(0)) != null : hashMapMessage.get(keyAndValue.get(0)) != null) {
					// System.out.println("Failed item");
					incrementCounter("unprocessedCount");
					System.out.println(newFileLine + " can't be added, it's key is present in the " + fileName + " JS file.");
					responseValues.put("errorMessage", responseValues.get("errorMessage").concat("<p>"+newFileLine + " can't be added, it's key is present in the " + fileName + " JS file.</p>"));
					LabelMessageValidator.unprocessed++;
				} else if (isLabel ? inverseHashMapLabel.get(keyAndValue.get(1)) != null : inverseHashMapMessage.get(keyAndValue.get(1)) != null) {
					// System.out.println("Failed item");
					incrementCounter("unprocessedCount");
					String key = isLabel ? inverseHashMapLabel.get(keyAndValue.get(1)) : inverseHashMapMessage.get(keyAndValue.get(1));
					System.out.println(newFileLine + " can't be added, it's value is present in the " + fileName + " JS file. Use " + key + " : \"" + keyAndValue.get(1) + "\".");
					responseValues.put("errorMessage", responseValues.get("errorMessage").concat("<p>"+newFileLine + " can't be added, it's value is present in the " + fileName + " JS file. Use " + key + " : \"" + keyAndValue.get(1) + "\".</p>"));
					LabelMessageValidator.unprocessed++;
				} else if (keyAndValue.get(0).length() > 0 && keyAndValue.get(1).length() > 0) {
					if (isLabel) {
						incrementCounter("newLabelCount");
						hashMapLabel.put(keyAndValue.get(0), keyAndValue.get(1));
						inverseHashMapLabel.put(keyAndValue.get(1), keyAndValue.get(0));
						incrementCounter("processed");
						LabelMessageValidator.newLabelCount++;
						LabelMessageValidator.processed++;
					} else {
						incrementCounter("newMessageCount");
						hashMapMessage.put(keyAndValue.get(0), keyAndValue.get(1));
						inverseHashMapMessage.put(keyAndValue.get(1), keyAndValue.get(0));
						incrementCounter("processed");
						LabelMessageValidator.newMessageCount++;
						LabelMessageValidator.processed++;
					}
					allQuery.append("\n" + insertQueryGenerator(isLabel, keyAndValue.get(0), keyAndValue.get(1), "English"));
				}
			}

			createLabelOrMessageBr.close();

		} catch (IOException ex) {
			throw ex;
		}
	}

	public void writeUpdatedFile(File messagesEnFile, StringBuffer stringBuffer, boolean appendData) throws IOException {
		try {
			FileWriter writeFile = new FileWriter(messagesEnFile, appendData);
			writeFile.write(stringBuffer.toString());
			writeFile.close();
		} catch (IOException ex) {
			throw ex;
		}
	}

	public void writeUpdatedFile(File messagesEnFile, StringBuffer stringBuffer) throws IOException {
		writeUpdatedFile(messagesEnFile, stringBuffer, false);
	}

	public StringBuffer insertQueryGenerator(boolean isLabel, String key, String value, String language) {
		StringBuffer query = new StringBuffer();
		query.append("INSERT INTO Fusion_New_Label(fusion_language_id , label_key , label_base_value , label_display_value , label_type_id) ");
		query.append("( SELECT FL.fusion_language_id , '");
		query.append(key);
		query.append("' , '");
		query.append(value.replaceAll("'", "''"));
		query.append("' , '");
		query.append(value.replaceAll("'", "''"));
		query.append("' , ");
		query.append("LT.label_type_id FROM fusion_language FL, label_type LT ");
		query.append("WHERE FL.language_value = '");
		query.append(language);
		query.append("' AND LT.label_type_name='");
		query.append(isLabel ? "labels" : "messages");
		query.append("');");

		return query;
	}

	public void dbVerionGenerator() {
		getNewDBVersion();
		StringBuffer query = new StringBuffer();
		allQuery.append("\n\n");
		query.append("INSERT INTO db_version(Module_Name, Module_DDL_Version , Module_DML_Version , Created_Date_TS) VALUES ('core', '");
		query.append(LabelMessageValidator.propertyValues.get(PropertyConstants.DDL_VERSION));
		query.append("' , '");
		query.append(LabelMessageValidator.propertyValues.get(PropertyConstants.DML_VERSION));
		query.append("' , CURRENT_TIMESTAMP");
		query.append(");");
		query.append("\nGO");
		allQuery.append(query);
	}

	public StringBuffer updateQueryGenerator(boolean isLabel, String key, String value, String language) {
		StringBuffer query = new StringBuffer();
		query.append("Update Fusion_New_Label set Label_Display_Value = '");
		query.append(value.replaceAll("'", "''"));
		query.append("' WHERE label_key = '");
		query.append(key);
		query.append("' AND Fusion_Language_Id = (select Fusion_Language_Id from Fusion_Language where Language_Value = '");
		query.append(language);
		query.append("') AND Label_Type_Id = (select Label_Type_Id from Label_Type where Label_Type_Name = '");
		query.append(isLabel ? "labels" : "messages");
		query.append("');");

		return query;
	}

	public void process() throws IOException {

		StringBuffer labelsAppender = new StringBuffer();
		labelsAppender.append("g_uuiLabels={");
		labelsAppender.append("\n");
		for (Map.Entry<String, String> map : hashMapLabel.entrySet()) {
			labelsAppender.append(map.getKey() + " : \"" + map.getValue() + "\",");
			labelsAppender.append("\n");
		}
		labelsAppender.append("};");
		writeUpdatedFile(labelsEnFile, labelsAppender);

		StringBuffer messageAppender = new StringBuffer();
		messageAppender.append("var g_uuiMessages = {");
		messageAppender.append("\n");
		for (Map.Entry<String, String> map : hashMapMessage.entrySet()) {
			messageAppender.append(map.getKey() + " : \"" + map.getValue() + "\",");
			messageAppender.append("\n");
		}
		messageAppender.append("};");
		writeUpdatedFile(messagesEnFile, messageAppender);
		if (LabelMessageValidator.processed > 0) {
			dbVerionGenerator();
			writeUpdatedFile(queryFile, allQuery, true);
			svnCommitProcess();
		}

	}

	public List<String> returnKeyAndValue(String singleLine) {

		List<String> keyAndValue = new ArrayList<String>();
		if (singleLine.contains(":")) {
			String updateKey = "", updateValue = "";
			int startIndex = 0, lastIndex = 0;
			updateKey = singleLine.split(":", 2)[0].trim().replaceAll(" ", "_").toUpperCase();
			startIndex = singleLine.indexOf("\"");
			lastIndex = singleLine.lastIndexOf("\"");
			if (startIndex == -1 || lastIndex == -1) {
				startIndex = singleLine.indexOf("'");
				lastIndex = singleLine.lastIndexOf("'");
				if (startIndex == -1 || lastIndex == -1) {
					return keyAndValue;
				}
			}
			updateValue = singleLine.substring(startIndex + 1, lastIndex).trim();

			keyAndValue.add(updateKey.trim());
			keyAndValue.add(updateValue.trim());
		}
		return keyAndValue;
	}

	/*
	 * public static void main(String[] args) throws IOException { try {
	 * 
	 * LabelsAndMessagesUtil createAndUpdateOperations = new LabelsAndMessagesUtil();
	 * 
	 * createAndUpdateOperations.addLabelsOrMessagesInJs(true); createAndUpdateOperations.updateLabelsOrMessagesInJs(true);
	 * 
	 * createAndUpdateOperations.addLabelsOrMessagesInJs(false); createAndUpdateOperations.updateLabelsOrMessagesInJs(false);
	 * 
	 * createAndUpdateOperations.jsGenerator();
	 * 
	 * } catch (IOException ex) { throw ex; } }
	 */

	private void incrementCounter(String key) {
		try {
			Long count = Long.valueOf(responseValues.get(key));
			count++;
			responseValues.put(key, count.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getNewDBVersion() {
		try {
			String line;
			String lastline = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(queryFile)));
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.equalsIgnoreCase("GO")) {
					lastline = line;
				}
			}
			br.close();
			if (!lastline.trim().isEmpty() && lastline.contains("db_version")) {
				lastline = lastline.replaceAll(" ", "");
				lastline = lastline.replace("INSERTINTOdb_version(Module_Name,Module_DDL_Version,Module_DML_Version,Created_Date_TS)VALUES('core','", "");
				lastline = lastline.replace(",CURRENT_TIMESTAMP);", "");
				lastline = lastline.replaceAll("'", "");
				String ddlVerion = lastline.split(",")[0];
				String dmlVerion = lastline.split(",")[1];
				LabelMessageValidator.propertyValues.put(PropertyConstants.DDL_VERSION, ddlVerion.trim());
				LabelMessageValidator.propertyValues.put(PropertyConstants.DML_VERSION, getNewDMLVersion(dmlVerion));
			} else if (!lastline.trim().isEmpty()) {
				System.out.println("Unable to identify the DB Version!!!");
				System.out.println("DB version script for your reference:");
				System.out.println(lastline);
				getDBVersionFromUser();
			} else {
				System.out.println("Unable to identify the DB Version!!!");
				getDBVersionFromUser();
			}
		} catch (Exception e) {
			System.out.println("Unable to identify the DB Version!!!");
			getDBVersionFromUser();
		}
	}

	private void getDBVersionFromUser() {
		try {
			System.out.print("Enter current DDL version:");
			LabelMessageValidator.propertyValues.put(PropertyConstants.DDL_VERSION, new BufferedReader(new InputStreamReader(System.in)).readLine());
			System.out.print("\nEnter current DML version:");
			LabelMessageValidator.propertyValues.put(PropertyConstants.DML_VERSION, new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private String getNewDMLVersion(String dmlVersion) {
		StringBuilder newDMLVersion = new StringBuilder();
		String[] dmlVersonSplit = dmlVersion.trim().split("\\.");
		int subVersionCounter = 1;
		for (String subDMLVersion : dmlVersonSplit) {
			if (dmlVersonSplit.length == subVersionCounter) {
				Long versionToChange = Long.valueOf(subDMLVersion);
				versionToChange++;
				newDMLVersion.append(versionToChange);
			} else {
				newDMLVersion.append(subDMLVersion).append(".");
			}
			subVersionCounter++;
		}
		return newDMLVersion.toString();
	}
	
	private void svnCommitProcess() {
		try {
			System.out.println("**************Comment for commit opertaion**************");
			StringBuilder commitComment = new StringBuilder("Label and Message changes requested by ");
			int responseCounter = 1;
			int responseCount = LabelMessageValidator.responseMailValues.size();
			for (Map.Entry<String, HashMap<String, String>> entry : LabelMessageValidator.responseMailValues.entrySet()) {
				Long processed = Long.valueOf(entry.getValue().get("processed"));
				if (processed > 0) {
					if (responseCounter < responseCount) {
						commitComment.append(entry.getValue().get("requestorName")).append(", ");
					} else {
						commitComment.append(entry.getValue().get("requestorName")).append(".\n\n");
					}
				}
				responseCounter++;
			}
			System.out.println(commitComment);
			System.out.print("Commit the files in SVN and enter the committed revison number:");
			commitVersion = Long.valueOf(new BufferedReader(new InputStreamReader(System.in)).readLine());
		} catch (IOException ioe) {
			System.out.println("Invalid committed revison number");
			svnCommitProcess();
		}
	}
}
