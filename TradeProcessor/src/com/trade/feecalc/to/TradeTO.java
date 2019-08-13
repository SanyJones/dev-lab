/**
 * 
 */
package com.trade.feecalc.to;

import java.util.Date;

import com.trade.feecalc.constants.PriorityFlag;
import com.trade.feecalc.constants.TransactionType;
import com.trade.feecalc.utility.DateUtil;

/**
 * @author SanyJones
 *
 */
public class TradeTO {
	
	private String externalTransactionId;
	
	private String clientId;
	
	private String securityId;
	
	private TransactionType transactionType;
	
	private Date transactionDate;
	
	private double marketValue;
	
	private PriorityFlag priorityFlag;
	
	private double processingFee;
	
	/**
	 * @return the processingFee
	 */
	public double getProcessingFee() {
		return processingFee;
	}

	/**
	 * @param processingFee the processingFee to set
	 */
	public void setProcessingFee(double processingFee) {
		this.processingFee = processingFee;
	}

	/**
	 * @return the externalTransactionId
	 */
	public String getExternalTransactionId() {
		return externalTransactionId;
	}

	/**
	 * @param externalTransactionId the externalTransactionId to set
	 */
	public void setExternalTransactionId(String externalTransactionId) {
		this.externalTransactionId = externalTransactionId;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the securityId
	 */
	public String getSecurityId() {
		return securityId;
	}

	/**
	 * @param securityId the securityId to set
	 */
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

	/**
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the transactionDate
	 */
	public Date getTransactionDate() {
		return transactionDate;
	}

	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	/**
	 * @return the marketValue
	 */
	public double getMarketValue() {
		return marketValue;
	}

	/**
	 * @param marketValue the marketValue to set
	 */
	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	/**
	 * @return the priorityFlag
	 */
	public PriorityFlag getPriorityFlag() {
		return priorityFlag;
	}

	/**
	 * @param priorityFlag the priorityFlag to set
	 */
	public void setPriorityFlag(PriorityFlag priorityFlag) {
		this.priorityFlag = priorityFlag;
	}

	@Override
	public String toString() {
		return this.getClientId().concat(", ").concat(this.getTransactionType().toString()).concat(", ")
				.concat(DateUtil.formatDate(this.getTransactionDate())).concat(", ")
				.concat(this.getPriorityFlag().toString()).concat(", ").concat(String.valueOf(this.getProcessingFee()));
	}
	
}
