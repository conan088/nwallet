package com.bx.implatform.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.PathVariable ;
import org.springframework.web.bind.annotation.PostMapping ;
import org.springframework.web.bind.annotation.PutMapping ;
import org.springframework.web.bind.annotation.RequestBody ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RequestParam ;
import org.springframework.web.bind.annotation.RestController ;

import com.bx.implatform.entity.User;
import com.bx.implatform.mapper.UserMapper ;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.IUserService;
import com.bx.implatform.session.SessionContext;
import com.bx.implatform.session.UserSession;
import com.bx.implatform.util.BeanUtils;
import com.bx.implatform.vo.BindUserAddressVO ;
import com.bx.implatform.vo.OnlineTerminalVO;
import com.bx.implatform.vo.UserVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "用户")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final UserMapper userMapper;

    @GetMapping("/terminal/online")
    @ApiOperation(value = "判断用户哪个终端在线", notes = "返回在线的用户id的终端集合")
    public Result<List<OnlineTerminalVO>> getOnlineTerminal(@NotEmpty @RequestParam("userIds") String userIds) {
        return ResultUtils.success(userService.getOnlineTerminals(userIds));
    }


    @GetMapping("/self")
    @ApiOperation(value = "获取当前用户信息", notes = "获取当前用户信息")
    public Result<UserVO> findSelfInfo() {
        UserSession session = SessionContext.getSession();
        User user = userService.getById(session.getUserId());
        UserVO userVO = BeanUtils.copyProperties(user, UserVO.class);
        return ResultUtils.success(userVO);
    }


    @GetMapping("/find/{id}")
    @ApiOperation(value = "查找用户", notes = "根据id查找用户")
    public Result<UserVO> findById(@NotEmpty @PathVariable("id") Long id) {
        return ResultUtils.success(userService.findUserById(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息，仅允许修改登录用户信息")
    public Result update(@Valid @RequestBody UserVO vo) {
        userService.update(vo);
        return ResultUtils.success();
    }
    @PostMapping("/bindAddress")
    @ApiOperation(value = "绑定用户钱包地址", notes = "绑定用户钱包地址，仅允许修改登录用户信息")
    public Result bindAddress(@Valid @RequestBody BindUserAddressVO vo) {
    	User user = new User() ;
    	user.setAddress(vo.getAddress());
    	user.setId(vo.getId());
    	userMapper.updateById(user);
    	return ResultUtils.success();
    }

    @GetMapping("/findByName")
    @ApiOperation(value = "查找用户", notes = "根据用户名或昵称查找用户")
    public Result<List<UserVO>> findByName(@RequestParam("name") String name) {
        return ResultUtils.success(userService.findUserByName(name));
    }
}

