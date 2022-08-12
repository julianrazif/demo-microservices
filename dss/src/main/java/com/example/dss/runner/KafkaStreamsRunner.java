package com.example.dss.runner;

import com.example.dss.config.KafkaConfigData;
import com.example.dss.dto.ArtistsDto;
import com.example.dss.model.KafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component("kafkaStreamsRunner")
public class KafkaStreamsRunner implements StreamsRunner<Long, String> {

  private KafkaStreams kafkaStreams;
  private final Serde<Long> keySerde = Serdes.Long();
  private final Serde<String> valueSerdeConsumer = new Serdes.StringSerde();
  private final Serde<String> valueSerdeProducer = new Serdes.StringSerde();
  private final KafkaConfigData kafkaConfigData;
  private final Properties streamsConfiguration;

  public KafkaStreamsRunner(KafkaConfigData kafkaConfigData,
                            @Qualifier("streamsConfiguration") Properties streamsConfiguration) {
    this.kafkaConfigData = kafkaConfigData;
    this.streamsConfiguration = streamsConfiguration;
  }

  @PreDestroy
  public void close() {
    if (kafkaStreams != null) {
      kafkaStreams.close();
      log.info("Kafka streaming closed!");
    }
  }

  @Override
  public void start() {
    log.info("Kafka streaming started . . .");
    final StreamsBuilder streamsBuilder = new StreamsBuilder();
    final Topology topology = createTopology(streamsBuilder);
    log.info("Defined topology: {}", topology.describe());
    kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
    kafkaStreams.start();
    log.info("Kafka streaming started..");
  }

  private Topology createTopology(StreamsBuilder streamsBuilder) {
    KStream<Long, String> kStream = getKafkaMessageKStream(streamsBuilder);

    kStream
      .filter((k, v) -> {
        KafkaMessage message = new KafkaMessage();

        try {
          message = new ObjectMapper().readValue(v, KafkaMessage.class);
        } catch (JsonProcessingException ex) {
          ex.printStackTrace();
        }

        return "apiGetArtists".equals(message.getMethod());
      })
      .mapValues((aLong, record) -> {
        KafkaMessage message = new KafkaMessage();

        try {
          message = new ObjectMapper().readValue(record, KafkaMessage.class);
        } catch (JsonProcessingException ex) {
          ex.printStackTrace();
        }

        if (!"julianraziffigaro".equals(message.getClientCode())) {
          return new Gson().toJson(new KafkaMessage());
        }

        ArtistsDto dto = new ObjectMapper().convertValue(message.getData(), ArtistsDto.class);
        List<ArtistsDto> artistsDtoList = new ArrayList<>();
        artistsDtoList.add(dto);

        KafkaMessage finalMessage = new KafkaMessage();
        finalMessage.setKey(message.getKey());
        finalMessage.setClientCode(message.getClientCode());
        finalMessage.setMethod(message.getMethod());
        finalMessage.setMessageId(message.getMessageId());
        finalMessage.setEc(0);
        finalMessage.setData(artistsDtoList);

        return new Gson().toJson(finalMessage);
      })
      .to(kafkaConfigData.getDssApiTopic() + "-apiArtists", Produced.with(keySerde, valueSerdeProducer));

    return streamsBuilder.build();
  }

  private KStream<Long, String> getKafkaMessageKStream(StreamsBuilder streamsBuilder) {
    return streamsBuilder.stream(kafkaConfigData.getApiDssTopic(), Consumed.with(Serdes.Long(), valueSerdeConsumer));
  }
}
