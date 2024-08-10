package com.xFly.IMServer.common.websocket.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Description:
 */
@Data
public class WSBaseResp<T> {

    private Integer type;
    private T data;
}
