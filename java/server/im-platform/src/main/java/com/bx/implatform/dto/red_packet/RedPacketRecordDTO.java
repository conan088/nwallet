package com.bx.implatform.dto.red_packet ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;
import lombok.EqualsAndHashCode ;

@Data
@EqualsAndHashCode(callSuper = false)
public class RedPacketRecordDTO extends PageQueryDTO{
	private static final long serialVersionUID = 1L ;

	@ApiModelProperty("钱包地址")
	private String address ;
}