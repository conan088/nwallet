package com.bx.implatform.dto.red_packet;

import java.io.Serializable ;
import java.util.List ;

import com.bx.implatform.entity.RedPacket ;
import com.bx.implatform.entity.RedPacketRecord ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;
@Data
public class RedPacketResultDTO implements Serializable {
	
	private static final long serialVersionUID = 1L ;
	
	@ApiModelProperty(value ="红包信息")
	private RedPacket redPacket;
	
	@ApiModelProperty(value ="领取列表")
	private List<RedPacketRecord> redPacketRecordList;
	
	@ApiModelProperty(value ="已领取数量")
	private Long receive_num;
	
	@ApiModelProperty(value ="我是否已经领取")
	private Boolean is_receive;
}
