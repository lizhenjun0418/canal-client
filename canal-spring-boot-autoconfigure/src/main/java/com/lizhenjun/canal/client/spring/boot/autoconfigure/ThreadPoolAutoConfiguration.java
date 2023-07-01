package com.lizhenjun.canal.client.spring.boot.autoconfigure;

import com.lizhenjun.canal.client.handler.CanalThreadUncaughtExceptionHandler;
import com.lizhenjun.canal.client.spring.boot.properties.CanalProperties;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
public class ThreadPoolAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("canal-execute-thread-%d")
                .uncaughtExceptionHandler(new CanalThreadUncaughtExceptionHandler()).build();
        return Executors.newFixedThreadPool(20, factory);
    }
}
