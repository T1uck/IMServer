package com.xFly.IMServer.common.user.domain.vo.Resp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * Description: 徽章信息
 */
@Data
@ApiOperation("徽章信息")
public class BadgeResp {

    @ApiModelProperty(value = "徽章id")
    private Long id;


    @ApiModelProperty(value = "徽章图标")
    private String img;


    @ApiModelProperty(value = "徽章描述")
    private String describe;


    @ApiModelProperty(value = "是否拥有 0否 1是")
    private Integer obtain;


    @ApiModelProperty(value = "是否佩戴 0否 1是")
    private Integer wearing;
}
