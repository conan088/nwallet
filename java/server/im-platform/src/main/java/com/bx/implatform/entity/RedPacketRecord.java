package com.bx.implatform.entity ;

import java.io.Serializable ;
import java.math.BigDecimal ;
import java.util.Date ;

import com.baomidou.mybatisplus.annotation.IdType ;
import com.baomidou.mybatisplus.annotation.TableId ;
import com.baomidou.mybatisplus.annotation.TableName ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;

/**
 * 抢红包记录表
 */
@Data
@TableName("red_packet_record")
public class RedPacketRecord implements Serializable{

	private static final long serialVersionUID = 1L ;

	@TableId(type = IdType.AUTO)
	private Long id ;

	private BigDecimal amount ;

	private String red_packet_id ;

	private String txid ;

	@ApiModelProperty(value = "0 未执行  2 成功 1失败")
	private Integer status ;

	//发红包人的钱包地址
	private String address ;

	//接收人的钱包地址
	private String target_address ;

	@ApiModelProperty(value = "接收红包聊天用户ID")
	private Long receive_user_id ;
	
	@ApiModelProperty(value = "发红包聊天用户ID")
	private Long send_user_id ;

	private Date create_date ;

	private Date update_date ;
}