package com.xFly.IMServer.common.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.xFly.IMServer.common.common.domain.vo.resp.ApiResult;
import com.xFly.IMServer.common.common.handler.GlobalUncaughtExceptionHandler;
import com.xFly.IMServer.common.user.dao.UserDao;
import com.xFly.IMServer.common.user.domain.dto.IpResult;
import com.xFly.IMServer.common.user.domain.entity.IpDetail;
import com.xFly.IMServer.common.user.domain.entity.IpInfo;
import com.xFly.IMServer.common.user.domain.entity.User;
import com.xFly.IMServer.common.user.service.IpService;
import com.xFly.IMServer.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description: ip服务实现类
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService , DisposableBean {
    @Resource
    private UserDao userDao;

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));

    /**
     * 异步更新用户详情
     *
     * @param uid 用户id
     */
    @Override
    public void refreshIpDetailAsync(Long uid) {
        EXECUTOR.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (Objects.isNull(ip)) {
                return;
            }
            IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes(ip);
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip,uid);
            }
        });
    }

    /**
     * 尝试获取ip详情，最多尝试3次
     *
     * @param ip 当前用户ip
     * @return 当前ip详情
     */
    private static IpDetail TryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("TryGetIpDetailOrNullTreeTimes InterruptedException", e);
            }
        }
        return null;
    }

    /**
     * 获取ip详情，基于淘宝开放接口
     *
     * @param ip
     * @return
     */
    private static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        } catch (Exception exception) {
            log.error("getIpDetailOrNull Exception", exception);
        }
        return null;
    }

    /**
     * 优雅停机
     * @throws InterruptedException
     */
    @Override
    public void destroy() throws InterruptedException {
        EXECUTOR.shutdown();
        if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {//最多等30秒，处理不完就拉倒
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }
        }
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = TryGetIpDetailOrNullTreeTimes("113.90.36.126");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (date.getTime() - begin.getTime())));
                }
            });
        }
    }
}
