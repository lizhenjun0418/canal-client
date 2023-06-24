package com.lizhenjun.canal.client.context;


import com.lizhenjun.canal.client.model.CanalModel;

/**
 * @author yang peng
 * @date 2019/3/2711:17
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
