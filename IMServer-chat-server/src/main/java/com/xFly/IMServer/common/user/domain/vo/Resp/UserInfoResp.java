package com.xFly.IMServer.common.user.domain.vo.Resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息返回
 */
@Data
@ApiModel("用户详情")
public class UserInfoResp {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long id;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String name;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    /**
     * 性别 1为男性，2为女性
     */
    @ApiModelProperty(value = "性别 1为男性，2为女性")
    private Integer sex;

    /**
     * 剩余改名次数
     */
    @ApiModelProperty(value = "剩余改名次数")
    private Integer modifyNameChance;
}
