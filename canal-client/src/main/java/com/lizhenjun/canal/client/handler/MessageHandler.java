package com.lizhenjun.canal.client.handler;

/**
 * @Description: 单表消息处理接口
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:21
 */
public interface MessageHandler<T> {

    /**
     * 
     * @Param t 
     * @return: 
 * @Return void
     */
    void handleMessage(T t);
}
