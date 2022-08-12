package com.example.dss;

import com.example.dss.runner.KafkaStreamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.example.dss"})
public class DssApplication implements CommandLineRunner {

  private final KafkaStreamsRunner kafkaStreamsRunner;

  public DssApplication(KafkaStreamsRunner kafkaStreamsRunner) {
    this.kafkaStreamsRunner = kafkaStreamsRunner;
  }

  public static void main(String[] args) {
    SpringApplication.run(DssApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Start app . . .");
    this.kafkaStreamsRunner.start();
    log.info("Kafka streams started . . .");
  }
}
