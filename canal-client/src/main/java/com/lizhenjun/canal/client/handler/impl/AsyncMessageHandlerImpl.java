package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.lizhenjun.canal.client.handler.AbstractMessageHandler;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * @Description: 异步数据处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:34
 */
public class AsyncMessageHandlerImpl extends AbstractMessageHandler {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<CanalEntry.RowData> rowDataHandler, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        super(entryHandlers, rowDataHandler);
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void handleMessage(Message message) {
        threadPoolTaskExecutor.execute(() -> super.handleMessage(message));
    }
}
