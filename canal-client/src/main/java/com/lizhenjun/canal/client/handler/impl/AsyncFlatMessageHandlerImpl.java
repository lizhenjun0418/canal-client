package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.lizhenjun.canal.client.handler.AbstractFlatMessageHandler;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;

/**
 * @Description: 异步扁平化数据处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:32
 */
@Slf4j
public class AsyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncFlatMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<List<Map<String, String>>> rowDataHandler, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        super(entryHandlers, rowDataHandler);
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        log.info("当前队列线程数 {} 堆积数量 {}", threadPoolTaskExecutor.getActiveCount(), threadPoolTaskExecutor.getQueueSize());
        threadPoolTaskExecutor.execute(() -> super.handleMessage(flatMessage));
    }
}
