package com.xFly.IMServer.common.user.dao;

import com.xFly.IMServer.common.user.domain.entity.Black;
import com.xFly.IMServer.common.user.mapper.user.BlackMapper;
import com.xFly.IMServer.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/T1uck">xFly</a>
 * @since 2024-09-15
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
