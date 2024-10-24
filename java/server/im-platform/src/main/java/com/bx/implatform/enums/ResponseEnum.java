package com.bx.implatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public enum ResponseEnum {
	
	SUCCESS(200,"成功")
    ,FAIL(500,"失败")
    ,EXE_FAIL(515,"需自己处理的失败")
    ,UN_LOGIN(401,"The login status has expired, please login in again")
    ,UN_REG(402,"该账号已注册,请直接登录")
    ,GOOGLE_AUTH(403,"进入安全验证页面 data{1:密码2:邮箱验证码3:谷歌验证码}")
	;

    @Getter
    @Setter
    private int code;
    
    @Getter
    @Setter
    private String msg;

}
