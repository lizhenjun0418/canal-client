package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.lizhenjun.canal.client.handler.AbstractFlatMessageHandler;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;

import java.util.List;
import java.util.Map;

/**
 * @Description: 异步数据处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 17:26
 */
public class SyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {

    public SyncFlatMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(entryHandlers, rowDataHandler);
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        super.handleMessage(flatMessage);
    }
}
