package com.bx.implatform.entity ;

import java.io.Serializable ;
import java.math.BigDecimal ;
import java.util.Date ;

import com.baomidou.mybatisplus.annotation.IdType ;
import com.baomidou.mybatisplus.annotation.TableField ;
import com.baomidou.mybatisplus.annotation.TableId ;
import com.baomidou.mybatisplus.annotation.TableName ;

import io.swagger.annotations.ApiModelProperty ;
import lombok.Data ;
import lombok.NoArgsConstructor ;
import lombok.experimental.Accessors ;

@Data
@NoArgsConstructor
@TableName("transaction")
@Accessors(chain = true)
public class Transaction implements Serializable{

	private static final long serialVersionUID = 1L ;

	@TableId(type = IdType.ASSIGN_UUID)
	private String id ;

	private String address ;

	private BigDecimal amount ;

	private String target_address ;

	private String txid ;

	private String order_id ;

	private String fee ;

	private String error ;

	private String remark ;

	@ApiModelProperty(value = "状态：0：转账中 1：成功 2：失败")
	private int status ;

	@TableField(exist = false)
	@ApiModelProperty(value = "类型：1 转出 2 转入")
	private Integer type ;

	private Date create_date ;

	private Date update_date ;

}
