/**
 * 
 */
package com.trade.feecalc.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.trade.feecalc.constants.PriorityFlag;
import com.trade.feecalc.constants.FeeCalculatorConstants;
import com.trade.feecalc.constants.TransactionType;
import com.trade.feecalc.to.TradeTO;
import com.trade.feecalc.utility.DateUtil;
import com.trade.feecalc.utility.FeeCalculatorUtil;

/**
 * @author SanyJones
 *
 */
public class CSVDataProcessorHandler implements IBaseDataProcessor {
	
	private List<TradeTO>  inputtradeList;
	private List<TradeTO>  outputTradeList;
	private Properties configProperties;
	
	
	
	BufferedReader bufferedReader = null;

	@Override
	public void init(Properties configProperties) {
		this.configProperties = configProperties;
	}

	@Override
	public void process() {
		inputtradeList = new ArrayList<TradeTO>();
		String row = "";
		int rowCount = 0;
        try {
        	bufferedReader = new BufferedReader(new FileReader(configProperties.getProperty(FeeCalculatorConstants.INPUT_FILE_LOCATION)));
            while ((row = bufferedReader.readLine()) != null) {
            	if(rowCount == 0){
            		rowCount ++;
            		continue;
            	}
            	int increment = 0;
            	String[] dataArray = row.split(configProperties.getProperty(FeeCalculatorConstants.CSV_SEPARATOR));
            	TradeTO tradeTO = new TradeTO();
            	tradeTO.setExternalTransactionId(dataArray[increment++]);
            	tradeTO.setClientId(dataArray[increment++]);
            	tradeTO.setSecurityId(dataArray[increment++]);
            	tradeTO.setTransactionType(TransactionType.valueOf(dataArray[increment++]));
            	tradeTO.setTransactionDate(DateUtil.parseDate(dataArray[increment++]));
            	tradeTO.setMarketValue(Double.valueOf(dataArray[increment++]));
            	tradeTO.setPriorityFlag(PriorityFlag.valueOf(dataArray[increment++]));
            	inputtradeList.add(tradeTO);
            }
            FeeCalculatorUtil feeCalculatorUtil = new FeeCalculatorUtil(inputtradeList);
            feeCalculatorUtil.setProcessingFeeForTrades();
            outputTradeList = feeCalculatorUtil.consolidateTrades();
        } catch (FileNotFoundException e) {
        	throw new RuntimeException("Unable to find input file", e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	if (bufferedReader != null) {
                try {
                	bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	@Override
	public void export() {
		FileWriter csvWriter = null;
		try {
			csvWriter = new FileWriter(configProperties.getProperty(FeeCalculatorConstants.OUTPUT_FILE_LOCATION));
			csvWriter.append(createHeader());
			csvWriter.append("\n");
			for (TradeTO currentTrade : outputTradeList) {
				csvWriter.append(currentTrade.toString());
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (Exception e) {
			throw new RuntimeException("Unable to export", e);
		}
	}
	
	private String createHeader() {
		return FeeCalculatorConstants.CLIENT_ID.concat(", ").concat(FeeCalculatorConstants.TRANSACTION_TYPE)
				.concat(", ").concat(FeeCalculatorConstants.TRANSACTION_DATE).concat(", ")
				.concat(FeeCalculatorConstants.PRIORITY).concat(", ")
				.concat(FeeCalculatorConstants.PROCESSING_FEE);
	}
}
