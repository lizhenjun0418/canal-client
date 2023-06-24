package com.lizhenjun.canal.client.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.lizhenjun.canal.client.client.AbstractCanalClient;
import com.lizhenjun.canal.client.context.CanalContext;
import com.lizhenjun.canal.client.model.CanalModel;
import com.lizhenjun.canal.client.util.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author yang peng
 * @date 2019/3/2921:38
 */
public abstract class AbstractMessageHandler implements MessageHandler<Message> {

    private Map<String, EntryHandler> tableHandlerMap;
    private RowDataHandler<CanalEntry.RowData> rowDataHandler;

    private Logger log = LoggerFactory.getLogger(AbstractCanalClient.class);

    public AbstractMessageHandler(List<? extends EntryHandler> entryHandlers, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(Message message) {
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            if (!CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                log.info("entity type: {}", entry.getEntryType());
                continue;
            }
            try {
                EntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, entry.getHeader().getTableName());
                if (entryHandler != null) {
                    CanalModel model = CanalModel.Builder.builder().id(message.getId()).table(entry.getHeader().getTableName())
                            .executeTime(entry.getHeader().getExecuteTime()).database(entry.getHeader().getSchemaName()).build();
                    CanalContext.setModel(model);
                    CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                    CanalEntry.EventType eventType = rowChange.getEventType();
                    // ddl
                    if (rowChange.getIsDdl()) {
                        rowDataHandler.handlerDDLData(rowChange.getSql(), entryHandler, eventType);
                        continue;
                    }
                    List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                    for (CanalEntry.RowData rowData : rowDataList) {
                        rowDataHandler.handlerRowData(rowData, entryHandler, eventType);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
            } finally {
                CanalContext.removeModel();
            }
        }
    }
}
