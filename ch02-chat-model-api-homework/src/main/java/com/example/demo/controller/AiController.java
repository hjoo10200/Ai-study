package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController 
@RequestMapping ("/ai")
@Slf4j 
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService){
        this.aiService = aiService;
    }

 @PostMapping(
      value = "/chat1",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String chat1(@RequestParam("question") String question) {
    String answer = aiService.chat1(question);
    return answer;
  }

    // ##### 요청 매핑 메소드 #####
  @PostMapping(
      value = "/chat2",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<String> chat2(@RequestParam("question") String question) {
    Flux<String> answer = aiService.chat2(question);
    return answer;
  }
}
