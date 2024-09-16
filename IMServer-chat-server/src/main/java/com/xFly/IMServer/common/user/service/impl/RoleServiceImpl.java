package com.xFly.IMServer.common.user.service.impl;

import com.xFly.IMServer.common.user.domain.enums.RoleEnum;
import com.xFly.IMServer.common.user.service.IRoleService;
import com.xFly.IMServer.common.user.service.cache.UserCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@Service
public class RoleServiceImpl implements IRoleService {
    @Resource
    private UserCache userCache;

    @Override
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(RoleEnum.ADMIN.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return Objects.requireNonNull(roleSet).contains(RoleEnum.ADMIN.getId());
    }
}
