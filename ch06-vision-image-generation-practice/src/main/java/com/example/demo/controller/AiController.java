package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.Aiservice;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController 
@RequestMapping ("/ai")
@Slf4j 
public class AiController {

  private final Aiservice aiService;

  public AiController(Aiservice aiService) {
    this.aiService = aiService;
  }

  @PostMapping(
    value = "/image-analysis",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_NDJSON_VALUE
  )
  public Flux<String> imageAnalysis(
    @RequestParam("question") String question,
    @RequestParam(value = "attach",required = false) MultipartFile attach) throws Exception{
  
    if (attach == null || attach.isEmpty()){
      Flux<String> answer = Flux.just("please, upload image.");
      return answer;
    }

    Flux<String> answer = aiService.imageAnalysis(question,attach.getContentType(),attach.getBytes());
    return answer;

   
  }
}
