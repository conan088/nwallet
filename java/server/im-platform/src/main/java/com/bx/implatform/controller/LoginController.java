package com.bx.implatform.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.PutMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestHeader ;
import org.springframework.web.bind.annotation.RestController ;

import com.bx.implatform.dto.LoginDTO;
import com.bx.implatform.dto.ModifyPwdDTO;
import com.bx.implatform.dto.RegisterDTO;
import com.bx.implatform.dto.UpdateUserDTO ;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.IUserService;
import com.bx.implatform.vo.LoginVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "用户登录和注册")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final IUserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public Result register(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return ResultUtils.success(vo);
    }


    @PutMapping("/refreshToken")
    @ApiOperation(value = "刷新token", notes = "用refreshtoken换取新的token")
    public Result refreshToken(@RequestHeader("refreshToken") String refreshToken) {
        LoginVO vo = userService.refreshToken(refreshToken);
        return ResultUtils.success(vo);
    }
    @PostMapping("/refreshToken2")
    @ApiOperation(value = "刷新token", notes = "用refreshtoken换取新的token")
    public Result refreshToken2(@Valid @RequestBody UpdateUserDTO dto) {
    	LoginVO vo = userService.refreshToken2(dto.getUserId());
    	return ResultUtils.success(vo);
    }


    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public Result register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return ResultUtils.success();
    }

    @PutMapping("/modifyPwd")
    @ApiOperation(value = "修改密码", notes = "修改用户密码")
    public Result update(@Valid @RequestBody ModifyPwdDTO dto) {
        userService.modifyPassword(dto);
        return ResultUtils.success();
    }

}
