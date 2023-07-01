package com.lizhenjun.canal.client.client;

/**
 * @Description: Canal客户端
 * @Author: lizhenjun
 * @Date: 2023/7/1 17:28
 */
public interface CanalClient {

    /**
     * 启动
     * @Return void
     */
    void start();

    /**
     * 停止
     * @Return void
     */
    void stop();

    /**
     * 消息处理
     * @Return void
     */
    void process();
}
