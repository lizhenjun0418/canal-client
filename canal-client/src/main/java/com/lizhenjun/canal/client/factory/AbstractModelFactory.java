package com.lizhenjun.canal.client.factory;

import com.lizhenjun.canal.client.enums.TableNameEnum;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.util.GenericUtil;
import com.lizhenjun.canal.client.util.HandlerUtil;

/**
 * @Description: 
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:46
 */
public abstract class AbstractModelFactory<T> implements IModelFactory<T> {

    @Override
    public <R> R newInstance(EntryHandler entryHandler, T t) throws Exception {
        String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
        if (TableNameEnum.ALL.name().equalsIgnoreCase(canalTableName)) {
            return (R) t;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            return newInstance(tableClass, t);
        }
        return null;
    }

    abstract <R> R newInstance(Class<R> c, T t) throws Exception;
}
