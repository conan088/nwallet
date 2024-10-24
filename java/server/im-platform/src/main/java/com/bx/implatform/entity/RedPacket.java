package com.bx.implatform.entity ;

import java.io.Serializable ;
import java.math.BigDecimal ;
import java.util.Date ;

import javax.validation.constraints.Min ;
import javax.validation.constraints.NotEmpty ;
import javax.validation.constraints.NotNull ;

import com.baomidou.mybatisplus.annotation.IdType ;
import com.baomidou.mybatisplus.annotation.TableId ;
import com.baomidou.mybatisplus.annotation.TableName ;

import io.swagger.annotations.ApiModel ;
import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;
import lombok.experimental.Accessors ;

@Data
@Accessors(chain = true)
@TableName("red_packet")
@ApiModel
public class RedPacket implements Serializable{

	private static final long serialVersionUID = 1L ;

	@TableId(type = IdType.ASSIGN_UUID)
	@ApiModelProperty(value = "Id主键")
	private String id ;

	@ApiModelProperty(value = "发送总金额")
	private BigDecimal amount ;

	@ApiModelProperty(value = "红包剩余未领取")
	private BigDecimal balance ;

	@NotNull(message = "num 不能为空")
	@ApiModelProperty(value = "红包个数")
	@Min(value = 1)
	private Integer num ;

	@ApiModelProperty(value = "备注")
	private String remark ;

	@ApiModelProperty(value = "Hash")
	private String hash ;

	@NotNull(message = "type 不能为空")
	@ApiModelProperty(value = "属性 0：个人红包 1：群红包")
	private Integer type ;

	@NotEmpty(message = "发红包钱包地址")
	@ApiModelProperty(value = "发红包钱包地址")
	private String address ;

	@ApiModelProperty(value = "状态 0：未领取 1：全部领取 2：退回 3:链上处理中")
	private Integer status ;

	@ApiModelProperty(value = "发红包聊天用户ID")
	private Long send_user_id ;

	private Date create_date ;

	private Date update_date ;
}