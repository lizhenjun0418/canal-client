package com.lizhenjun.canal.client.context;


import com.lizhenjun.canal.client.model.CanalModel;

/**
 * @Description: canal上下文
 * @Author: lizhenjun
 * @Date: 2023/7/1 17:27
 */
public class CanalContext {
    private static ThreadLocal<CanalModel> threadLocal = new ThreadLocal<>();

    public static CanalModel getModel() {
        return threadLocal.get();
    }

    public static void setModel(CanalModel canalModel) {
        threadLocal.set(canalModel);
    }

    public static void removeModel() {
        threadLocal.remove();
    }
}
