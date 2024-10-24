package com.bx.implatform.dto.red_packet ;

import java.io.Serializable ;
import java.math.BigDecimal ;

import javax.validation.constraints.Max ;
import javax.validation.constraints.Min ;
import javax.validation.constraints.NotEmpty ;
import javax.validation.constraints.NotNull ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;

/**
 * P2P发红包请求
 */
@Data
public class SendRedPacketDTO implements Serializable{

	private static final long serialVersionUID = 1L ;

	//	@DecimalMin(value = "0.01")
	//	@DecimalMax(value = "100000")
	@ApiModelProperty(value = "发送总金额")
	protected BigDecimal amount ;

	@ApiModelProperty(value = "属性 0：个人红包 1：群红包")
	@NotNull(message = "type 不能为空 ")
	private Integer type ;

	@ApiModelProperty(value = "红包个数 P2R的时候前端传")
	@Min(value = 1, message = "红包个数不能小于1")
	@Max(value = 50, message = "红包个数不能大于50")
	private int num = 1 ;

	@ApiModelProperty(value = "Hash")
	@NotNull(message = "Hash 不能为空 ")
	private String hash ;

	@NotEmpty(message = "remark 不能为空")
	@ApiModelProperty(value = "备注")
	private String remark ;
	

}