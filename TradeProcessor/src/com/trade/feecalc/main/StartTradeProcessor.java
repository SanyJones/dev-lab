/**
 * 
 */
package com.trade.feecalc.main;

import java.util.Properties;

import com.trade.feecalc.handlers.CSVDataProcessorHandler;
import com.trade.feecalc.handlers.IBaseDataProcessor;
import com.trade.feecalc.utility.PropertyUtility;

/**
 * @author SanyJones
 *
 */
public class StartTradeProcessor {

	public static void main(String args[]) {
		Properties configProperties = PropertyUtility.loadProperties();
		IBaseDataProcessor dataProcessor = new CSVDataProcessorHandler();
		dataProcessor.init(configProperties);
		dataProcessor.process();
		dataProcessor.export();
	}

}
