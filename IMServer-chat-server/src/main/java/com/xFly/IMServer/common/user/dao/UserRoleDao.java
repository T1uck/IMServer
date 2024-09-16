package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.UserRole;
import com.xFly.IMServer.common.user.mapper.user.UserRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

    /**
     * 获取用户角色
     * @param uid
     * @return
     */
    public List<UserRole> listById(Long uid) {
        return lambdaQuery().eq(UserRole::getUid, Objects.requireNonNull(uid)).list();
    }
}
