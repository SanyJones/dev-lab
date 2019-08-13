/**
 * 
 */
package com.trade.feecalc.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author SanyJones
 *
 */
public class PropertyUtility {
	
	public static Properties loadProperties(){
		Properties properties;
		try {
			FileReader propertyReader = new FileReader("resource/configuration.properties");
			properties =new Properties();  
			properties.load(propertyReader); 
		} catch (FileNotFoundException e) {
			throw new RuntimeException("property file is not found", e);
		} catch (IOException e) {
			throw new RuntimeException("Error in processing property file", e);
		}
		return properties; 
	}

}
