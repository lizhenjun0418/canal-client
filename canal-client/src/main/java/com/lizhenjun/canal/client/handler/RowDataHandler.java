package com.lizhenjun.canal.client.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @Description: 多表消息处理接口
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:22
 */
public interface RowDataHandler<T> {

    /**
     * DML消息处理
     * @Param t
     * @Param entryHandler
     * @Param eventType
     * @Return void
     */
    <R> void handlerRowData(T t, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) throws Exception;

    /**
     * DDL消息处理
     * @Param sql
     * @Param entryHandler
     * @Param eventType
     * @Return void
     */
    <R> void handlerDDLData(String sql, EntryHandler<R> entryHandler, CanalEntry.EventType eventType);
}
