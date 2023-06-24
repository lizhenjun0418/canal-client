package com.lizhenjun.canal.client.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lizhenjun.canal.client.factory.IModelFactory;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class MapRowDataHandlerImpl implements RowDataHandler<List<Map<String, String>>> {

    private IModelFactory<Map<String, String>> modelFactory;

    public MapRowDataHandlerImpl(IModelFactory<Map<String, String>> modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handlerRowData(List<Map<String, String>> list, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) throws Exception {
        log.info("处理消息 {}", list);
        if (entryHandler != null) {
            return;
        }
        switch (eventType) {
            case INSERT:
                R entry = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.insert(entry);
                break;
            case UPDATE:
                R before = modelFactory.newInstance(entryHandler, list.get(1));
                R after = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.update(before, after);
                break;
            case DELETE:
                R o = modelFactory.newInstance(entryHandler, list.get(0));
                entryHandler.delete(o);
                break;
            default:
                log.info("未知消息类型 {} 不处理 {}", eventType, list);
                break;
        }
    }

    @Override
    public <R> void handlerDDLData(String sql, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) {
        entryHandler.ddl(sql);
    }
}
