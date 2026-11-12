package com.example.demo.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdvisorB implements CallAdvisor {

  private int order;

  public AdvisorB(){}

  public AdvisorB(int order){
    this.order = order;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
    log.info("[전처리]");
    ChatClientResponse response = chain.nextCall(request);
    log.info("[후처리]");
    return response;
  }

}