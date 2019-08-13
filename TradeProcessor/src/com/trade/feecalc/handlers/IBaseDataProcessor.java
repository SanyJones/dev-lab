/**
 * 
 */
package com.trade.feecalc.handlers;

import java.util.Properties;

/**
 * @author SanyJones
 *
 */
public interface IBaseDataProcessor {
	
	void init(Properties configProperties);
	
	void process();
	
	void export();
	

}
