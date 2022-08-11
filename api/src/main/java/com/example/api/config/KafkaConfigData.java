package com.example.api.config;

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
  private boolean enableAutoCommit;
  private String autoOffsetReset;
  private Boolean batchListener;
  private Boolean autoStartup;
  private Integer concurrencyLevel;
  private Integer sessionTimeoutMs;
  private Integer heartbeatIntervalMs;
  private Integer maxPollIntervalMs;
  private Integer maxPollRecords;
  private Integer maxPartitionFetchBytesDefault;
  private Integer maxPartitionFetchBytesBoostFactor;
  private Long pollTimeoutMs;
  private String DssApiTopic;
  private String apiDssTopic;
}
