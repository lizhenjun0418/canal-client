package com.lizhenjun.canal.client.util;

import com.lizhenjun.canal.client.handler.EntryHandler;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Table;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 
 * @Author: lizhenjun
 * @Date: 2023/7/1 14:53
 */
public class GenericUtil {

    private static Map<Class<? extends EntryHandler>, Class> cache = new ConcurrentHashMap<>();
    
    /**
     * 查找实体类的table注解的类名
     * @Param entryHandler
     * @Return java.lang.String
     */
    static String getTableGenericProperties(EntryHandler entryHandler) {
        Class<?> tableClass = getTableClass(entryHandler);
        if(null == tableClass) {
            return StringUtils.EMPTY;
        }
        Table annotation = tableClass.getAnnotation(Table.class);
        return null != annotation ? annotation.name() : StringUtils.EMPTY;
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTableClass(EntryHandler object) {
        Class<? extends EntryHandler> handlerClass = object.getClass();
        Class tableClass = cache.get(handlerClass);
        if (null != tableClass) {
            return tableClass;
        }
        Type[] interfacesTypes = handlerClass.getGenericInterfaces();
        for (Type t : interfacesTypes) {
            Class c = (Class) ((ParameterizedType) t).getRawType();
            if (c.equals(EntryHandler.class)) {
                tableClass = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
                cache.putIfAbsent(handlerClass, tableClass);
                return tableClass;
            }
        }
        return null;
    }
}
