package com.lizhenjun.canal.client.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.lizhenjun.canal.client.handler.MessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Description: Canal启动入口
 * @Author: lizhenjun
 * @Date: 2023/7/1 13:58
 */
public abstract class AbstractCanalClient implements CanalClient {

    protected volatile boolean flag;

    private Thread workThread;

    private CanalConnector connector;

    /**
     * 监听表规，如果canal-server设置了监听规则，这里就不要设置，否则会覆盖服务端的规则
     */
    protected String filter = StringUtils.EMPTY;

    /**
     * 获取指定数量的数据
     */
    protected Integer batchSize = 1;
    /**
     * 超时时间，单位秒
     */
    protected Long timeout = 1L;

    protected TimeUnit unit = TimeUnit.SECONDS;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 消息处理器
     */
    private MessageHandler messageHandler;

    /**
     * 启动监听
     * @Return void
     */
    @Override
    public void start() {
        log.info("start canal client");
        workThread = new Thread(this::process);
        workThread.setName("canal-client-thread");
        flag = true;
        workThread.start();
    }

    /**
     * 停止监听
     * @Return void
     */
    @Override
    public void stop() {
        log.info("stop canal client");
        flag = false;
        if (null != workThread) {
            workThread.interrupt();
        }
    }

    /**
     * 消息处理
     * @Return void
     */
    @Override
    public void process() {
        while (flag) {
            try {
                connector.connect();
                connector.subscribe(filter);
                while (flag) {
                    Message message = connector.getWithoutAck(batchSize, timeout, unit);
                    log.info("获取消息 {}", message);
                    long batchId = message.getId();
                    if (message.getId() != -1 && message.getEntries().size() != 0) {
                        messageHandler.handleMessage(message);
                    }
                    connector.ack(batchId);
                }
            } finally {
                connector.disconnect();
            }
        }
    }

    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public CanalConnector getConnector() {
        return connector;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
