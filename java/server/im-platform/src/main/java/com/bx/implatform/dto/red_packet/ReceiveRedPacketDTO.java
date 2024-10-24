package com.bx.implatform.dto.red_packet ;

import javax.validation.constraints.NotEmpty ;

import lombok.Data ;

/**
 * 接收红包请求对象
 */
@Data
public class ReceiveRedPacketDTO{
	@NotEmpty(message = "红包id不能为空")
	private String id ;
}