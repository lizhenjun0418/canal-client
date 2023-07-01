package com.lizhenjun.canal.example.handler;

import com.alibaba.fastjson2.JSON;
import com.lizhenjun.canal.client.annotation.CanalTable;
import com.lizhenjun.canal.client.context.CanalContext;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.example.domin.User;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@CanalTable(value = "user")
public class UserHandler implements EntryHandler<User> {

    @Resource
    DSLContext dslContext;

    @Override
    public void insert(User user) {
        String userJsonString = JSON.toJSONString(user);
        log.info("insert message  {}", userJsonString);
        String table = CanalContext.getModel().getTable();
        Map<String, Object> userMap = JSON.parseObject(userJsonString, HashMap.class);
        List<Field<Object>> fields = userMap.keySet().stream().map(DSL::field).collect(Collectors.toList());
        List<Param<Object>> values = userMap.values().stream().map(DSL::value).collect(Collectors.toList());
        int execute = dslContext.insertInto(DSL.table(table)).columns(fields).values(values).execute();
        log.info("执行结果 {}", execute);
    }

    @Override
    public void update(User before, User after) {
        String userBeforeJsonString = JSON.toJSONString(before);
        String userAfterJsonString = JSON.toJSONString(after);
        log.info("update before {} ", userBeforeJsonString);
        log.info("update after {}", userAfterJsonString);
        String table = CanalContext.getModel().getTable();
        Map<String, Object> userAfterMap = JSON.parseObject(userAfterJsonString, Map.class);

        Map<Field<Object>, Object> map = userAfterMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> DSL.field(entry.getKey()), Map.Entry::getValue));
        int execute = dslContext.update(DSL.table(table)).set(map).where(DSL.field("id").eq(userAfterMap.get("id"))).execute();
        log.info("执行结果 {}", execute);
    }

    @Override
    public void delete(User user) {
        log.info("delete  {}", JSON.toJSONString(user));
        String table = CanalContext.getModel().getTable();
        dslContext.delete(DSL.table(table)).where(DSL.field("id").eq(user.getId())).execute();
    }

    @Override
    public void ddl(String sql) {
        log.info("ddl sql {}", sql);
        dslContext.execute(sql);
    }
}
