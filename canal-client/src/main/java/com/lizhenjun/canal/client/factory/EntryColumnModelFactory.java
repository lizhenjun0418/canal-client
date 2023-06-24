package com.lizhenjun.canal.client.factory;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.lizhenjun.canal.client.enums.TableNameEnum;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.util.EntryUtil;
import com.lizhenjun.canal.client.util.FieldUtil;
import com.lizhenjun.canal.client.util.GenericUtil;
import com.lizhenjun.canal.client.util.HandlerUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yang peng
 * @date 2019/3/2916:16
 */
public class EntryColumnModelFactory extends AbstractModelFactory<List<CanalEntry.Column>> {

    @Override
    public <R> R newInstance(EntryHandler entryHandler, List<CanalEntry.Column> columns) throws Exception {
        String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
        if (TableNameEnum.ALL.name().equalsIgnoreCase(canalTableName)) {
            Map<String, String> map = columns.stream().collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
            return (R) map;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            return newInstance(tableClass, columns);
        }
        return null;
    }

    @Override
    public <R> R newInstance(EntryHandler entryHandler, List<CanalEntry.Column> columns, Set<String> updateColumn) throws Exception {
        String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
        if (TableNameEnum.ALL.name().equalsIgnoreCase(canalTableName)) {
            Map<String, String> map = columns.stream().filter(column -> updateColumn.contains(column.getName()))
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
            return (R) map;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass == null) {
            return null;
        }
        R r = tableClass.newInstance();
        Map<String, String> columnNames = EntryUtil.getFieldName(r.getClass());
        for (CanalEntry.Column column : columns) {
            if (!updateColumn.contains(column.getName())) {
                continue;
            }
            String fieldName = columnNames.get(column.getName());
            if (!StringUtils.isEmpty(fieldName)) {
                continue;
            }
            FieldUtil.setFieldValue(r, fieldName, column.getValue());
        }
        return r;
    }

    @Override
    <R> R newInstance(Class<R> c, List<CanalEntry.Column> columns) throws Exception {
        R object = c.newInstance();
        Map<String, String> columnNames = EntryUtil.getFieldName(object.getClass());
        for (CanalEntry.Column column : columns) {
            String fieldName = columnNames.get(column.getName());
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }
            FieldUtil.setFieldValue(object, fieldName, column.getValue());
        }
        return object;
    }
}
