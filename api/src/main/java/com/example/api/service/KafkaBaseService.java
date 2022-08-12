package com.example.api.service;

import com.example.api.config.KafkaConfigData;
import com.example.api.model.KafkaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("kafkaBaseService")
public class KafkaBaseService {

  private final KafkaConfigData kafkaConfigData;
  private final ReplyingKafkaTemplate<Long, String, String> replyingKafkaTemplate;

  public KafkaBaseService(KafkaConfigData kafkaConfigData,
                          ReplyingKafkaTemplate<Long, String, String> replyingKafkaTemplate) {
    this.kafkaConfigData = kafkaConfigData;
    this.replyingKafkaTemplate = replyingKafkaTemplate;
  }

  public KafkaMessage sendKafka(String method, Object data, Long waitTime) {
    Long key = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    String clientCode = "julianraziffigaro";
    String messageId = method + key;

    KafkaMessage kafkaMessage = new KafkaMessage(
      key,
      clientCode,
      method,
      messageId,
      0,
      data
    );

    RequestReplyFuture<Long, String, String> target = getRequestReplyFuture(kafkaMessage, waitTime);
    ListenableFuture<SendResult<Long, String>> sent = sent(target);

    SendResult<Long, String> sendResult = null;
    try {
      sendResult = sent.get(waitTime, TimeUnit.SECONDS);
    } catch (ExecutionException | InterruptedException ex) {
      log.error("ExecutionException | InterruptedException error sending the message and the exception is {}", ex.getMessage());
      Thread.currentThread().interrupt();
    } catch (Exception ex) {
      log.error("Error sending the message and the exception is {}", ex.getMessage());
    }

    if (sendResult == null) {
      log.error("Can not send the message, producer error");
      return new KafkaMessage();
    }

    ConsumerRecord<Long, String> kafkaResponse = null;
    try {
      kafkaResponse = target.get(waitTime, TimeUnit.SECONDS);
    } catch (ExecutionException | InterruptedException ex) {
      log.error("ExecutionException | InterruptedException error receive the message and the exception is {}", ex.getMessage());
      Thread.currentThread().interrupt();
    } catch (Exception ex) {
      log.error("Error receive the message and the exception is {}", ex.getMessage());
    }

    if (kafkaResponse == null) {
      log.error("Can not receive the message, Timeout");
      return new KafkaMessage();
    }

    KafkaMessage finalMessage = new KafkaMessage();
    try {
      finalMessage = new ObjectMapper().readValue(kafkaResponse.value(), KafkaMessage.class);
    } catch (JsonProcessingException ex) {
      ex.printStackTrace();
    }

    if (!messageId.equals(finalMessage.getMessageId())) {
      return null;
    }

    return finalMessage;
  }

  private RequestReplyFuture<Long, String, String> getRequestReplyFuture(KafkaMessage kafkaMessage, Long waitTime) {
    ProducerRecord<Long, String> producerRecord = new ProducerRecord<>(kafkaConfigData.getApiDssTopic(), kafkaMessage.getKey(), new Gson().toJson(kafkaMessage));
    RequestReplyFuture<Long, String, String> replyFuture = replyingKafkaTemplate.sendAndReceive(producerRecord, Duration.ofSeconds(waitTime));
    replyFuture.addCallback(new ListenableFutureCallback<>() {
      @Override
      public void onFailure(@NonNull Throwable ex) {
        log.error("Error receive the message and the exception is {}", ex.getMessage());
        try {
          throw ex;
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      @Override
      public void onSuccess(ConsumerRecord<Long, String> result) {
        log.info("Received -> key={}, value={}, headers={}", result.key(), result.value(), result.headers());
      }
    });

    return replyFuture;
  }

  private ListenableFuture<SendResult<Long, String>> sent(RequestReplyFuture<Long, String, String> target) {
    ListenableFuture<SendResult<Long, String>> sendFuture = target.getSendFuture();
    sendFuture.addCallback(new ListenableFutureCallback<>() {
      @Override
      public void onFailure(@NonNull Throwable ex) {
        log.error("Error sending the message and the exception is {}", ex.getMessage());
        try {
          throw ex;
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      @Override
      public void onSuccess(SendResult<Long, String> result) {
        log.info("Sent     -> key={}, value={}, headers={}", result.getProducerRecord().key(), result.getProducerRecord().value(), result.getProducerRecord().headers());
      }
    });

    return sendFuture;
  }
}
