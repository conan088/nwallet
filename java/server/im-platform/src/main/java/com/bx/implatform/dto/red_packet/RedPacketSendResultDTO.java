package com.bx.implatform.dto.red_packet;

import java.io.Serializable ;

import com.baomidou.mybatisplus.core.metadata.IPage ;
import com.bx.implatform.entity.RedPacket ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;
@Data
public class RedPacketSendResultDTO implements Serializable {
	
	private static final long serialVersionUID = 1L ;
	
	@ApiModelProperty(value ="红包记录分页列表")
	private IPage<RedPacket> pages;
	
	@ApiModelProperty(value ="领取红包的总金额")
	private Object sum;
	
}
