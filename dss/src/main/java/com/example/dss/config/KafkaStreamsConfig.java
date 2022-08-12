package com.example.dss.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class KafkaStreamsConfig {

  private final KafkaConfigData kafkaConfigData;

  public KafkaStreamsConfig(KafkaConfigData kafkaConfigData) {
    this.kafkaConfigData = kafkaConfigData;
    log.info("ConsumerConfig={}", kafkaConfigData.toString());
  }

  @Bean(name = "streamsConfiguration")
  public Properties streamsConfiguration() {
    Properties streamsConfiguration = new Properties();
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfigData.getGroupId());
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, kafkaConfigData.getClientId());
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConfigData.getAutoOffsetReset());
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
    streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, kafkaConfigData.getConcurrencyLevel());
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, kafkaConfigData.getCommitIntervalMs());
    return streamsConfiguration;
  }
}
