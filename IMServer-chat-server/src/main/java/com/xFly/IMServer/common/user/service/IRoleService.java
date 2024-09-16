package com.xFly.IMServer.common.user.service;

import com.xFly.IMServer.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xFly.IMServer.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 */
public interface IRoleService{

    /**
     * 是否有某个权限，临时做法
     * @param uid 用户id
     * @param roleEnum 角色枚举
     * @return 是否存在
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
