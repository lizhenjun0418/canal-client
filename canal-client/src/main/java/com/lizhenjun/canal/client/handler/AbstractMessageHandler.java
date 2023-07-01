package com.lizhenjun.canal.client.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.lizhenjun.canal.client.context.CanalContext;
import com.lizhenjun.canal.client.model.CanalModel;
import com.lizhenjun.canal.client.util.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/***
 * @Description: 消息处理业务处理，字节码数据，传输效率高，消息需要反序列化
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:19
 */
public abstract class AbstractMessageHandler implements MessageHandler<Message> {

    private Map<String, EntryHandler> tableHandlerMap;
    private RowDataHandler<CanalEntry.RowData> rowDataHandler;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
                if (null == entryHandler) {
                    continue;
                }
                CanalModel model = CanalModel.Builder.builder().id(message.getId()).table(entry.getHeader().getTableName())
                        .executeTime(entry.getHeader().getExecuteTime()).database(entry.getHeader().getSchemaName()).build();
                CanalContext.setModel(model);
                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                CanalEntry.EventType eventType = rowChange.getEventType();
                // ddl数据处理
                if (rowChange.getIsDdl()) {
                    rowDataHandler.handlerDDLData(rowChange.getSql(), entryHandler, eventType);
                    continue;
                }
                //dml数据处理
                List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                for (CanalEntry.RowData rowData : rowDataList) {
                    rowDataHandler.handlerRowData(rowData, entryHandler, eventType);
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
            } finally {
                CanalContext.removeModel();
            }
        }
    }
}
