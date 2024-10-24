package com.bx.implatform.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息VO")
public class BindUserAddressVO {

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(value = "id")
    private Long id;


    @ApiModelProperty(value = "钱包地址")
    private String address;

}
