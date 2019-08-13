/**
 * 
 */
package com.trade.feecalc.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trade.feecalc.constants.FeeCalculatorConstants;
import com.trade.feecalc.constants.TransactionType;
import com.trade.feecalc.to.TradeTO;

/**
 * @author SanyJones
 *
 */
public class FeeCalculatorUtil {
	
	private List<TradeTO> allTrades;
	
	public FeeCalculatorUtil(List<TradeTO> allTrades){
		this.allTrades = allTrades;
	}
	
	
	class SortTrade implements Comparator<TradeTO> 
	{ 
		public int compare(TradeTO trade1, TradeTO trade2) {
			if (trade1.getClientId().compareTo(trade2.getClientId()) > 0
					&& trade1.getTransactionType().compareTo(trade2.getTransactionType()) > 0
					&& trade1.getTransactionDate().compareTo(trade2.getTransactionDate()) > 0
					&& trade1.getPriorityFlag().compareTo(trade2.getPriorityFlag()) > 0) {

			} else {
				return -1;
			}
			return 0;
		}
	} 
	private boolean isIntraDayTrade(TradeTO currentTrade) {
		for (TradeTO trade : allTrades) {
			if (trade.getClientId().equals(currentTrade.getClientId())
					&& trade.getSecurityId().equals(currentTrade.getSecurityId())
					&& trade.getTransactionDate().compareTo(currentTrade.getTransactionDate()) == 0) {
				if (trade.getTransactionType().compareTo(TransactionType.BUY) == 0
						&& currentTrade.getTransactionType().compareTo(TransactionType.SELL) == 0
						|| trade.getTransactionType().compareTo(TransactionType.SELL) == 0
								&& currentTrade.getTransactionType().compareTo(TransactionType.BUY) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private double getTransactionFee(TradeTO currentTrade) {
		double transactionFee = 0.0;
		if (isIntraDayTrade(currentTrade)) {
			transactionFee = FeeCalculatorConstants.FIVFTY_DOLLARS;
		} else {
			switch (currentTrade.getPriorityFlag()) {
			case Y:
				transactionFee = FeeCalculatorConstants.FIVE_HUNDRED_DOLLARS;
				break;
			case N:
				if (currentTrade.getTransactionType().compareTo(TransactionType.SELL) == 0
						|| currentTrade.getTransactionType().compareTo(TransactionType.WITHDRAW) == 0) {
					transactionFee = FeeCalculatorConstants.HUNDRED_DOLLARS;
					break;
				} else {
					transactionFee = FeeCalculatorConstants.FIVFTY_DOLLARS;
					break;
				}
			}
		}
		return transactionFee;
	}
	
	public List<TradeTO> setProcessingFeeForTrades(){
		for (TradeTO currentTrade : allTrades) {
			currentTrade.setProcessingFee(getTransactionFee(currentTrade));
		}
		return allTrades;
	}

	public List<TradeTO> consolidateTrades() {
		List<TradeTO> consolidatedTradesList = new ArrayList<TradeTO>();
		for (TradeTO currentTrade : allTrades) {
			if (consolidatedTradesList.isEmpty()) {
				consolidatedTradesList.add(currentTrade);
			} else {
				for (TradeTO trade : consolidatedTradesList)
					if (trade.getClientId().equals(currentTrade.getClientId())
							&& trade.getTransactionType().compareTo(currentTrade.getTransactionType()) == 0
							&& trade.getTransactionDate().compareTo(currentTrade.getTransactionDate()) == 0
							&& trade.getPriorityFlag().compareTo(currentTrade.getPriorityFlag()) == 0) {
						double processingFee = currentTrade.getProcessingFee() + trade.getProcessingFee();
						currentTrade.setProcessingFee(processingFee);
					}
				consolidatedTradesList.add(currentTrade);
			}
		}
		Collections.sort(consolidatedTradesList, new SortTrade());
		return consolidatedTradesList;
	}
}
