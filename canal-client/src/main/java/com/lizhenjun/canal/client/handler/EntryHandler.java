package com.lizhenjun.canal.client.handler;

/**
 * @Description: 单表消息处理接口
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:26
 */
public interface EntryHandler<T> {

    /**
     * 新增
     * @Param t
     * @Return void
     */
    default void insert(T t) {

    }

    /**
     * 修改
     * @Param t
     * @Return void
     */
    default void update(T before, T after) {

    }

    /**
     * 删除
     * @Param t
     * @Return void
     */
    default void delete(T t) {

    }

    /**
     * ddl
     * @Param t
     * @Return void
     */
    default void ddl(String sql) {

    }

}
