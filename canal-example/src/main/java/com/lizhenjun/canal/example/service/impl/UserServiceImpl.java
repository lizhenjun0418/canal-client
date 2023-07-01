package com.lizhenjun.canal.example.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lizhenjun.canal.example.service.IUserService;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import com.lizhenjun.canal.example.domin.User;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    DSLContext dslContext;

    @Override
    public int insert(User user) {
        String userJsonString = JSON.toJSONString(user);
        Map<String, Object> userMap = JSON.parseObject(userJsonString, HashMap.class);
        List<Field<Object>> fields = userMap.keySet().stream().map(DSL::field).collect(Collectors.toList());
        List<Param<Object>> values = userMap.values().stream().map(DSL::value).collect(Collectors.toList());
        return dslContext.insertInto(DSL.table("user")).columns(fields).values(values).execute();
    }

    @Override
    public int update(User user) {
        String userJsonString = JSON.toJSONString(user);
        Map<String, Object> userMap = JSON.parseObject(userJsonString, Map.class);
        Map<Field<Object>, Object> map = userMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> DSL.field(entry.getKey()), Map.Entry::getValue));
        return dslContext.update(DSL.table("user")).set(map).where(DSL.field("id").eq(user.getId())).execute();
    }

    @Override
    public void delete(Long id) {
        dslContext.delete(DSL.table("user")).where(DSL.field("id").eq(id)).execute();
    }

    @Override
    public void executeSql(String sql) {
        dslContext.execute(sql);
    }
}
