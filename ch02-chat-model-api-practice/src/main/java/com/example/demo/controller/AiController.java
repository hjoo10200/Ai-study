package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiService;
import com.example.demo.service.AiServiceByChatClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {

  private final AiServiceByChatClient aiServiceByChatClient;
  private final AiService aiService;

  public AiController(AiService aiService,AiServiceByChatClient aiServiceByChatClient) {
    this.aiService = aiService;
    this.aiServiceByChatClient = aiServiceByChatClient;
  }


  
  // ##### 요청 매핑 메소드 #####
  @PostMapping(
      value = "/chat1",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String chat1(@RequestParam("question") String question) {
    String answer = aiServiceByChatClient.chat1(question);
    return answer;
  }

    // ##### 요청 매핑 메소드 #####
  @PostMapping(
      value = "/chat2",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<String> chat2(@RequestParam("question") String question) {
    Flux<String> answer = aiServiceByChatClient.chat2(question);
    return answer;
  }
}
