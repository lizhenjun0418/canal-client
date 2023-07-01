package com.lizhenjun.canal.example.handler;

import com.lizhenjun.canal.client.annotation.CanalTable;
import com.lizhenjun.canal.client.context.CanalContext;
import com.lizhenjun.canal.client.handler.EntryHandler;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 获取到map 对象后转换成sql，使用jooq执行 sql
 * @Author: lizhenjun
 * @Date: 2023/7/1 17:38
 */
@Slf4j
@CanalTable(value = "all")
@Component
public class DefaultEntryHandler implements EntryHandler<Map<String, String>> {

    @Resource
    DSLContext dslContext;

    @Override
    public void insert(Map<String, String> map) {
        log.info("增加 {}", map);
        String table = CanalContext.getModel().getTable();
        List<Field<Object>> fields = map.keySet().stream().map(DSL::field).collect(Collectors.toList());
        List<Param<String>> values = map.values().stream().map(DSL::value).collect(Collectors.toList());
        int execute = dslContext.insertInto(DSL.table(table)).columns(fields).values(values).execute();
        log.info("执行结果 {}", execute);
    }

    @Override
    public void update(Map<String, String> before, Map<String, String> after) {
        log.info("修改 before {}", before);
        log.info("修改 after {}", after);
        String table = CanalContext.getModel().getTable();
        Map<Field<Object>, String> map = after.entrySet().stream().filter(entry -> before.get(entry.getKey()) != null)
                .collect(Collectors.toMap(entry -> DSL.field(entry.getKey()), Map.Entry::getValue));
        dslContext.update(DSL.table(table)).set(map).where(DSL.field("id").eq(after.get("id"))).execute();
    }

    @Override
    public void delete(Map<String, String> map) {
        log.info("删除 {}", map);
        String table = CanalContext.getModel().getTable();
        dslContext.delete(DSL.table(table)).where(DSL.field("id").eq(map.get("id"))).execute();
    }

    @Override
    public void ddl(String sql) {
        log.info("dl sql {}", sql);
        dslContext.execute(sql);
    }
}
