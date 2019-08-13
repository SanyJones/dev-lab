package com.dev.reader.property;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dev.reader.property.constants.PropertyConstants;

/**
 * 
 * @author Sany Jones R
 * @since 1.0
 */
public class PropertyReader
{
	public Map<String, String> getPropertyValues(Map<String, String> propertyMap) throws Exception {
		try {
			if(propertyMap == null){
				propertyMap = new HashMap<String, String>();
			}
			Properties properties = new Properties();
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertyMap.get(PropertyConstants.FILE_NAME));
			if (inputStream != null) {
				properties.load(inputStream);
			} else {
				throw new FileNotFoundException("File" + propertyMap.get(PropertyConstants.FILE_NAME) + " is not found");
			}
			Field[]  propertyConstants = PropertyConstants.class.getDeclaredFields();
			for(Field propertyConstant : propertyConstants) {
				if(properties.get(propertyConstant.get(null)) != null){
					propertyMap.put((String) propertyConstant.get(null), properties.getProperty((String) propertyConstant.get(null)));
				}
			}
			return propertyMap;
		} catch (Exception e) {
			throw new Exception("unable to get property values" + e);
		}
	}
}
