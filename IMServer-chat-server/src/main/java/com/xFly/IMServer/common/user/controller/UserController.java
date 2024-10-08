package com.xFly.IMServer.common.user.controller;


import com.xFly.IMServer.common.common.domain.dto.RequestInfo;
import com.xFly.IMServer.common.common.domain.vo.resp.ApiResult;
import com.xFly.IMServer.common.common.utils.RequestHolder;
import com.xFly.IMServer.common.user.domain.vo.Req.BlackUserReq;
import com.xFly.IMServer.common.user.domain.vo.Req.ModifyNameReq;
import com.xFly.IMServer.common.user.domain.vo.Req.WearingBadgeReq;
import com.xFly.IMServer.common.user.domain.vo.Resp.BadgeResp;
import com.xFly.IMServer.common.user.domain.vo.Resp.UserInfoResp;
import com.xFly.IMServer.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户管理相关接口")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("获取用户详情")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/modifyName")
    @ApiOperation("修改用户名")
    public ApiResult<Void> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(),req);
        return ApiResult.success();
    }

    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }

    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(), req);
        return ApiResult.success();
    }

    @PutMapping("/black")
    public ApiResult<Void> blackUser(@Valid @RequestBody BlackUserReq req) {
        // 判断当前用户是否有权限进行拉黑操作
        userService.hasPower(RequestHolder.get().getUid());
        // 拉黑目标用户
        userService.black(req);
        return ApiResult.success();
    }
}

