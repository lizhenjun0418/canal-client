package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lizhenjun.canal.client.factory.IModelFactory;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 单条数据处理
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:42
 */
@Slf4j
public class RowDataHandlerImpl implements RowDataHandler<CanalEntry.RowData> {

    private IModelFactory<List<CanalEntry.Column>> modelFactory;

    public RowDataHandlerImpl(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handlerRowData(CanalEntry.RowData rowData, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) throws Exception {
        log.info("RowDataHandlerImpl-处理消息 [{}]", rowData);
        if (null == entryHandler) {
            return;
        }
        switch (eventType) {
            case INSERT:
                R object = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                entryHandler.insert(object);
                break;
            case UPDATE:
                Set<String> updateColumnSet = rowData.getAfterColumnsList().stream().filter(CanalEntry.Column::getUpdated)
                        .map(CanalEntry.Column::getName).collect(Collectors.toSet());
                R before = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList(), updateColumnSet);
                R after = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                entryHandler.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList());
                entryHandler.delete(o);
                break;
            default:
                log.info("未知消息类型 {} 不处理 {}", eventType, JSON.toJSONString(rowData));
                break;
        }
    }

    @Override
    public <R> void handlerDDLData(String sql, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) {
        entryHandler.ddl(sql);
    }
}
