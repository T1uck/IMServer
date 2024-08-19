package com.xFly.IMServer.common.common.exception;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Charsets;
import com.xFly.IMServer.common.common.domain.vo.resp.ApiResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 业务校验异常码
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum{

    ACCESS_DENIED(401,"登陆失效，请重新登陆");

    private Integer httpCode;

    private String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(getErrorCode());
        response.setContentType(ContentType.JSON.toString(Charsets.UTF_8));
        ApiResult<HttpErrorEnum> responseData = ApiResult.fail(this);
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
