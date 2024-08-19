package com.xFly.IMServer.common.user.controller;


import com.xFly.IMServer.common.common.domain.dto.RequestInfo;
import com.xFly.IMServer.common.common.domain.vo.resp.ApiResult;
import com.xFly.IMServer.common.common.utils.RequestHolder;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import com.xFly.IMServer.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理相关接口")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/ModifyName")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(),req);
        return ApiResult.success();
    }
}

