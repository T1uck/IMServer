package com.xFly.IMServer.common.user.service.cache;


import com.xFly.IMServer.common.common.constant.RedisKey;
import com.xFly.IMServer.common.common.utils.RedisUtils;
import com.xFly.IMServer.common.user.dao.BlackDao;
import com.xFly.IMServer.common.user.dao.UserRoleDao;
import com.xFly.IMServer.common.user.domain.entity.Black;
import com.xFly.IMServer.common.user.domain.entity.UserRole;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户相关缓存
 */
@Component
public class UserCache {
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private BlackDao blackDao;


    @Cacheable(cacheNames = "user", key = "'roles:'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listById(uid);
        return userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
    }

    @Cacheable(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
        Map<Integer, List<Black>> blackMap = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        return blackMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet())));
    }

    @CacheEvict(cacheNames = "user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

}
