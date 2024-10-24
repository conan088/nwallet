package com.bx.implatform.dto ;

import java.math.BigDecimal ;

import lombok.AllArgsConstructor ;
import lombok.Data ;
import lombok.NoArgsConstructor ;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordsResultDTO{

	private String txid ;

	private String timestamp ;

	private String from ;

	private String to ;

	private BigDecimal value ;
	
	private String symbol ;
	private String blockNumber ;

	public TransactionRecordsResultDTO(String txid, String timestamp, String from, String to, BigDecimal value) {
		super() ;
		this.txid = txid ;
		this.timestamp = timestamp ;
		this.from = from ;
		this.to = to ;
		this.value = value ;
	}
	
	public TransactionRecordsResultDTO(String txid, String timestamp, String from, String to, BigDecimal value, String blockNumber) {
		super() ;
		this.txid = txid ;
		this.timestamp = timestamp ;
		this.from = from ;
		this.to = to ;
		this.value = value ;
		this.blockNumber = blockNumber ;
	}
}
