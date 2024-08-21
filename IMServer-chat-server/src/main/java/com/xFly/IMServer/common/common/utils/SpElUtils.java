package com.xFly.IMServer.common.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Description: spring el表达式解析
 */
public class SpElUtils {

    // 定义一个静态常量，用于解析SpEL表达式
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    // 定义一个静态常量，用于发现参数名称
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static String parseSpEl(Method method, Object[] args, String spEl) {
        // 解析参数名
        String[] params = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(new String[]{});
        // 解析SpEL表达式
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            // 所有参数都作为原材料仍进去
            standardEvaluationContext.setVariable(params[i], args[i]);
        }
        Expression expression = PARSER.parseExpression(spEl);
        return expression.getValue(standardEvaluationContext, String.class);
    }

    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }
}
