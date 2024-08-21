package com.xFly.IMServer.common.user.service.impl;


import com.xFly.IMServer.common.common.annotation.RedissonLock;
import com.xFly.IMServer.common.common.domain.enums.IdempotentEnum;
import com.xFly.IMServer.common.common.domain.enums.YesOrNoEnum;
import com.xFly.IMServer.common.user.dao.UserBackpackDao;
import com.xFly.IMServer.common.user.domain.entity.ItemConfig;
import com.xFly.IMServer.common.user.domain.entity.UserBackpack;
import com.xFly.IMServer.common.user.domain.enums.ItemTypeEnum;
import com.xFly.IMServer.common.user.service.IUserBackpackService;
import com.xFly.IMServer.common.user.service.cache.ItemCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 */
@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //组装幂等号
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, itemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000) //相同幂等如果同时发奖，需要排队等上一个执行完，取出之前数据返回
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        UserBackpack userBackpack = userBackpackDao.getByIdp(idempotent);
        //幂等检查
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //业务检查
        ItemConfig itemConfig = itemCache.getById(itemId);
        if (ItemTypeEnum.BADGE.getType().equals(itemConfig.getType())) { //徽章类型做唯一性检查
            Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, itemId);
            if (countByValidItemId > 0) {//已经有徽章了不发
                return;
            }
        }
        //发物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
        //用户收到物品的事件
//        applicationEventPublisher.publishEvent(new ItemReceiveEvent(this, insert));
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
