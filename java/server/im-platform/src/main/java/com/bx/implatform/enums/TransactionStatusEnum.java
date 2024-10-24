package com.bx.implatform.enums ;

import java.util.Objects ;

import com.baomidou.mybatisplus.annotation.EnumValue ;
import com.bx.implatform.util.CustomException ;
import com.fasterxml.jackson.annotation.JsonValue ;

import lombok.AllArgsConstructor ;
import lombok.Getter ;

@Getter
@AllArgsConstructor
public enum TransactionStatusEnum {

	LOADING(0, "转账中"),
	SUCCESS(1, "转账成功"),
	FAIL(2, "转账失败"),
	;

	@EnumValue//标记数据库存的值是code
	private Integer id ;
	@JsonValue
	private String name ;
	public static TransactionStatusEnum match(int role_id) {
    	for (TransactionStatusEnum e : TransactionStatusEnum.values()) {
    		if (Objects.equals(e.getId(), role_id)) {
    			return e;
    		}
    	}
    	throw new CustomException("状态不存在") ;
    }

}
