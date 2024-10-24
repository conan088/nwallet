package com.bx.implatform.dto ;

import javax.validation.constraints.NotEmpty ;
import javax.validation.constraints.NotNull ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;

@Data
public class ImportWalletDTO{

	@ApiModelProperty(value = "钱包种类ID（通过钱包种类接口查询）")
	@NotNull(message = "钱包种类ID不能为空")
	private Integer category ;

	@ApiModelProperty(value = "钱包密钥（私钥|助记词）")
	@NotEmpty(message = "钱包密钥不能为空")
	private String content ;
}
