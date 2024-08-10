package com.xFly.IMServer.commen;


import cn.hutool.json.JSONUtil;
import com.xFly.IMServer.common.IMServerCustomApplication;
import com.xFly.IMServer.common.common.utils.RedisUtils;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = IMServerCustomApplication.class)
@RunWith(SpringRunner.class)
public class DaoTest {
    @Resource
    private UserDao userDao;

    @Resource
    private RedissonClient redissonClient;
    @Test
    public void Redisson() {
        RLock rLock = redissonClient.getLock("123");
        rLock.lock();
        System.out.println();
        rLock.unlock();
    }

    @Test
    public void redis() {
        RedisUtils.set("name", "西兰花");
        String name = RedisUtils.getStr("name");
        System.out.println(name);
    }

    @Test
    public void test() {
        User byId = userDao.getById(1);
        User user = new User();
        user.setName("11");
        user.setOpenId("123");
        boolean save = userDao.save(user);
        System.out.println(save);
    }
}