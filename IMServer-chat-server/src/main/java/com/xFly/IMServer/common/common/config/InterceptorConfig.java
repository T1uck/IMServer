package com.xFly.IMServer.common.common.config;


import com.xFly.IMServer.common.common.interceptor.CollectorInterceptor;
import com.xFly.IMServer.common.common.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Description: 配置所有拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;

    @Resource
    private CollectorInterceptor collectorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/capi/**");
    }
}
