package com.bx.implatform.dto ;

import java.math.BigInteger ;

import io.swagger.annotations.ApiModel ;
import lombok.Data ;
import lombok.experimental.Accessors ;

@Data
@ApiModel
@Accessors(chain = true)
public class ExeTransferDTO{

	String from_address;
	String target_address;
	String private_key;
	String amount;
	String contract_address;
	BigInteger Gas;
	Integer Decimal;
}