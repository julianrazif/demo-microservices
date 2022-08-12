package com.example.dss.model;

import com.example.dss.dto.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessage implements DomainObject {

  private Long key;
  private String clientCode;
  private String method;
  private String messageId;
  private int ec;
  private Object data;
}
