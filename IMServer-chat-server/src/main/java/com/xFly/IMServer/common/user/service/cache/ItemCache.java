package com.xFly.IMServer.common.user.service.cache;


import com.xFly.IMServer.common.user.dao.ItemConfigDao;
import com.xFly.IMServer.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 物品相关缓存
 */
@Component
public class ItemCache {//todo 多级缓存

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }

    @Cacheable(cacheNames = "item", key = "'item:'+#itemId")
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
