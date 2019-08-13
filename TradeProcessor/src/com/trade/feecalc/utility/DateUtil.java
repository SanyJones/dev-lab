/**
 * 
 */
package com.trade.feecalc.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author SanyJones
 *
 */
public class DateUtil {
	
	private static final String dateFormatMMDDYYYY = "dd-mm-yy";

	public static Date parseDate(String dateString){
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormatMMDDYYYY);
		Date date = null;
		try {
			date = formatter.parse(dateString);
			return date;
		} catch (ParseException e) {
			throw new RuntimeException("Unable to parse date", e);
		}
	}
	
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormatMMDDYYYY);
		return formatter.format(date);
	}

}
