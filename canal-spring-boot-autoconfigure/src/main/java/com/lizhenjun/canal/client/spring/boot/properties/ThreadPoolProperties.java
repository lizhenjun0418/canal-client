package com.lizhenjun.canal.client.spring.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @ClassName ThreadPoolProperties
 * @Description 线程池配置
 * @Author lizhenjun
 * @Date 2023/7/1 19:15
 * @Version V1.0
 **/
//@Configuration
@ConfigurationProperties(prefix = ThreadPoolProperties.THREAD_POOL_PREFIX)
public class ThreadPoolProperties {

    // 核心线程池大小
    private int corePoolSize;

    // 最大可创建的线程数
    private int maxPoolSize;

    // 队列最大长度
    private int queueCapacity;

    // 线程池维护线程所允许的空闲时间
    private int keepAliveSeconds;

    public static final String THREAD_POOL_PREFIX = "thread";

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}
