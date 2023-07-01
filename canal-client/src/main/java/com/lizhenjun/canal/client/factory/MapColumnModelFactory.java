package com.lizhenjun.canal.client.factory;

import com.lizhenjun.canal.client.util.EntryUtil;
import com.lizhenjun.canal.client.util.FieldUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Description: 
 * @Author: lizhenjun
 * @Date: 2023/7/1 15:36
 */
public class MapColumnModelFactory extends AbstractModelFactory<Map<String, String>> {

    @Override
    <R> R newInstance(Class<R> c, Map<String, String> valueMap) throws Exception {
        R object = c.newInstance();
        Map<String, String> columnNames = EntryUtil.getFieldName(object.getClass());
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String fieldName = columnNames.get(entry.getKey());
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }
            FieldUtil.setFieldValue(object, fieldName, entry.getValue());
        }
        return object;
    }
}
