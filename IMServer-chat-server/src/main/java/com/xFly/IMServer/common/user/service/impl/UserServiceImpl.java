package com.xFly.IMServer.common.user.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.mapper.UserMapper;
import com.xFly.IMServer.common.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    @Resource
    private UserDao userDao;
    /**
     * 用户注册,需要获取id
     * @param user
     */
    @Override
    public void register(User user) {
        userDao.save(user);
    }
}
