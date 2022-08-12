package com.example.dss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {

  private String bootstrapServers;
  private String clientId;
  private String groupId;
  private String autoOffsetReset;
  private Integer concurrencyLevel;
  private Integer commitIntervalMs;
  private String DssApiTopic;
  private String apiDssTopic;
}
