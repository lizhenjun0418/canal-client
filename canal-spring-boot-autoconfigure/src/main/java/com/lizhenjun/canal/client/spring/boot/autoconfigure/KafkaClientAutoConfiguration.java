package com.lizhenjun.canal.client.spring.boot.autoconfigure;


import com.lizhenjun.canal.client.client.KafkaCanalClient;
import com.lizhenjun.canal.client.factory.MapColumnModelFactory;
import com.lizhenjun.canal.client.handler.EntryHandler;
import com.lizhenjun.canal.client.handler.MessageHandler;
import com.lizhenjun.canal.client.handler.RowDataHandler;
import com.lizhenjun.canal.client.handler.impl.AsyncFlatMessageHandlerImpl;
import com.lizhenjun.canal.client.handler.impl.MapRowDataHandlerImpl;
import com.lizhenjun.canal.client.handler.impl.SyncFlatMessageHandlerImpl;
import com.lizhenjun.canal.client.spring.boot.properties.CanalKafkaProperties;
import com.lizhenjun.canal.client.spring.boot.properties.CanalProperties;
import com.lizhenjun.canal.client.spring.boot.properties.CanalSimpleProperties;
import com.lizhenjun.canal.client.spring.boot.properties.ThreadPoolProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({CanalSimpleProperties.class, ThreadPoolProperties.class})
@ConditionalOnBean(value = {EntryHandler.class})
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "kafka")
@Import(ThreadPoolAutoConfiguration.class)
public class KafkaClientAutoConfiguration {

    private CanalKafkaProperties canalKafkaProperties;

    public KafkaClientAutoConfiguration(CanalKafkaProperties canalKafkaProperties) {
        this.canalKafkaProperties = canalKafkaProperties;
    }

    @Bean
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                         List<EntryHandler> entryHandlers,
                                         ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new AsyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler, threadPoolTaskExecutor);
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "false")
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers) {
        return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaCanalClient kafkaCanalClient(MessageHandler messageHandler) {
        return KafkaCanalClient.builder().servers(canalKafkaProperties.getServer())
                .groupId(canalKafkaProperties.getGroupId())
                .topic(canalKafkaProperties.getDestination())
                .messageHandler(messageHandler)
                .batchSize(canalKafkaProperties.getBatchSize())
                .filter(canalKafkaProperties.getFilter())
                .timeout(canalKafkaProperties.getTimeout())
                .unit(canalKafkaProperties.getUnit())
                .build();
    }
}
