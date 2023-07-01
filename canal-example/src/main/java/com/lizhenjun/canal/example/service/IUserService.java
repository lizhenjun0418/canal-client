package com.lizhenjun.canal.example.service;

import com.lizhenjun.canal.example.domin.User;

public interface IUserService {

    int insert(User user);

    int update(User user);

    void delete(Long id);

    void executeSql(String sql);
}
