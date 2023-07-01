package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.lizhenjun.canal.client.handler.AbstractMessageHandler;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @Description: 异步数据处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:34
 */
public class AsyncMessageHandlerImpl extends AbstractMessageHandler {

    private ExecutorService executor;

    public AsyncMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<CanalEntry.RowData> rowDataHandler, ExecutorService executor) {
        super(entryHandlers, rowDataHandler);
        this.executor = executor;
    }

    @Override
    public void handleMessage(Message message) {
        executor.execute(() -> super.handleMessage(message));
    }
}
