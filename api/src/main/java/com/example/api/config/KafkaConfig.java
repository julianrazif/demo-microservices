package com.example.api.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

  private final KafkaConfigData kafkaConfigData;

  public KafkaConfig(KafkaConfigData kafkaConfigData) {
    this.kafkaConfigData = kafkaConfigData;
    log.info("kafkaConfigData={}", kafkaConfigData.toString());
  }

  @Bean
  public ProducerFactory<Long, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    configProps.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfigData.getClientId() + "-Producer");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public ConsumerFactory<Long, String> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    configProps.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfigData.getClientId() + "-Consumer");
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfigData.getGroupId());
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfigData.getAutoOffsetReset());
    configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConfigData.isEnableAutoCommit());
    configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfigData.getSessionTimeoutMs());
    configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConfigData.getHeartbeatIntervalMs());
    configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConfigData.getMaxPollIntervalMs());
    configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, kafkaConfigData.getMaxPartitionFetchBytesDefault() * kafkaConfigData.getMaxPartitionFetchBytesBoostFactor());
    configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfigData.getMaxPollRecords());
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<Long, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Long, String>> containerFactory() {
    ConcurrentKafkaListenerContainerFactory<Long, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setReplyTemplate(kafkaTemplate());
    factory.setBatchListener(kafkaConfigData.getBatchListener());
    factory.setConcurrency(kafkaConfigData.getConcurrencyLevel());
    factory.setAutoStartup(kafkaConfigData.getAutoStartup());
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
    factory.getContainerProperties().setPollTimeout(kafkaConfigData.getPollTimeoutMs());
    return factory;
  }

  @Bean
  public ConcurrentMessageListenerContainer<Long, String> repliesContainer() {
    ConcurrentMessageListenerContainer<Long, String> repliesContainer = containerFactory().createContainer(kafkaConfigData.getDssApiTopic(),
      kafkaConfigData.getDssApiTopic() + "-apiArtists"
    );
    repliesContainer.getContainerProperties().setGroupId(kafkaConfigData.getGroupId());
    return repliesContainer;
  }

  @Bean
  public ReplyingKafkaTemplate<Long, String, String> replyingKafkaTemplate() {
    ReplyingKafkaTemplate<Long, String, String> replyingKafkaTemplate = new ReplyingKafkaTemplate<>(producerFactory(), repliesContainer());
    replyingKafkaTemplate.setSharedReplyTopic(true);
    replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofSeconds(30L));
    return replyingKafkaTemplate;
  }
}
