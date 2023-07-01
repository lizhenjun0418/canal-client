package com.lizhenjun.canal.client.handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: Canal线程异常处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:27
 */
@Slf4j
public class CanalThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("thread {} have a exception: {}", t.getName(), e);
    }
}
