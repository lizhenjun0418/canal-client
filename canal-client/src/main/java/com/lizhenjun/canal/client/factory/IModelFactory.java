package com.lizhenjun.canal.client.factory;

import com.lizhenjun.canal.client.handler.EntryHandler;

import java.util.Set;

/**
 * @Description:
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:32
 */
public interface IModelFactory<T> {

    <R> R newInstance(EntryHandler entryHandler, T t) throws Exception;

    default <R> R newInstance(EntryHandler entryHandler, T t, Set<String> updateColumn) throws Exception {
        return null;
    }
}
