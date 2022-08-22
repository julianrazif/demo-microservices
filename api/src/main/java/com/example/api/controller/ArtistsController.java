package com.example.api.controller;

import com.example.api.dto.ArtistsDto;
import com.example.api.dto.RequestDto;
import com.example.api.dto.ResponseDto;
import com.example.api.model.KafkaMessage;
import com.example.api.service.KafkaBaseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ArtistsController {

  private final KafkaBaseService kafkaBaseService;

  public ArtistsController(KafkaBaseService kafkaBaseService) {
    this.kafkaBaseService = kafkaBaseService;
  }

  @GetMapping(value = "/artists",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseDto<List<ArtistsDto>> getArtists(@RequestBody RequestDto<ArtistsDto> request, HttpServletRequest httpServletRequest) {
    if (httpServletRequest.getSession().getAttribute("artists") != null) {
      return new ResponseDto<>(
        new ObjectMapper().convertValue(httpServletRequest.getSession().getAttribute("artists"),
          new TypeReference<>() {
          })
      );
    }

    KafkaMessage kafkaMessage = kafkaBaseService.sendKafka("apiGetArtists", request.getData(), 30L);
    List<ArtistsDto> list = new ObjectMapper().convertValue(kafkaMessage.getData(), new TypeReference<>() {
    });

    httpServletRequest.getSession().setAttribute("artists", list);
    return new ResponseDto<>(list);
  }
}
