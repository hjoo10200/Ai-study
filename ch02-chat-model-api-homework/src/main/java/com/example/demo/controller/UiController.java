package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {
  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/chat1")
  public String chat1() {
    return "chat1";
  }
  @GetMapping("/chat2")
  public String chat2() {
    return "chat2";
  }
}
