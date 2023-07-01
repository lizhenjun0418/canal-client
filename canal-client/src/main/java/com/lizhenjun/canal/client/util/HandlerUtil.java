package com.lizhenjun.canal.client.util;


import com.lizhenjun.canal.client.annotation.CanalTable;
import com.lizhenjun.canal.client.enums.TableNameEnum;
import com.lizhenjun.canal.client.handler.EntryHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:50
 */
public class HandlerUtil {

    public static EntryHandler getEntryHandler(List<? extends EntryHandler> entryHandlers, String tableName) {
        EntryHandler globalHandler = null;
        for (EntryHandler handler : entryHandlers) {
            String canalTableName = getCanalTableName(handler);
            if (TableNameEnum.ALL.name().equalsIgnoreCase(canalTableName)) {
                globalHandler = handler;
                continue;
            }
            if (StringUtils.equalsIgnoreCase(tableName, canalTableName)) {
                return handler;
            }
            String name = GenericUtil.getTableGenericProperties(handler);
            if (StringUtils.equalsIgnoreCase(name, tableName)) {
                return handler;
            }
        }
        return globalHandler;
    }

    public static Map<String, EntryHandler> getTableHandlerMap(List<? extends EntryHandler> entryHandlers) {
        if (CollectionUtils.isEmpty(entryHandlers)) {
            return Collections.emptyMap();
        }
        Map<String, EntryHandler> map = new ConcurrentHashMap<>();
        for (EntryHandler handler : entryHandlers) {
            String canalTableName = getCanalTableName(handler);
            if (canalTableName != null) {
                map.putIfAbsent(canalTableName.toLowerCase(), handler);
                continue;
            }
            String name = GenericUtil.getTableGenericProperties(handler);
            if (name != null) {
                map.putIfAbsent(name.toLowerCase(), handler);
            }
        }
        return map;
    }

    public static EntryHandler getEntryHandler(Map<String, EntryHandler> map, String tableName) {
        EntryHandler entryHandler = map.get(tableName);
        if (null == entryHandler) {
            entryHandler = map.get(TableNameEnum.ALL.name().toLowerCase());
        }
        return entryHandler;
    }
    
    /**
     * 获取Handler CanalTable注解的value值（数据库表名）
     * @Param entryHandler
     * @Return java.lang.String
     */
    public static String getCanalTableName(EntryHandler entryHandler) {
        CanalTable canalTable = entryHandler.getClass().getAnnotation(CanalTable.class);
        return null == canalTable ? null : canalTable.value();
    }
}
