package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.AiService;
import com.example.demo.dto.Hotel;
import com.example.demo.dto.Pizza;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/list-output-converter")
    public List<String> listOutputConverter(@RequestParam("question") String question) {
        List<String> list = aiService.listOutputConverter(question);
        return list;
    }

    @PostMapping("/bean-output-converter")
    public Hotel beanOutputConverter(@RequestParam("question") String question) {
        Hotel hotel = aiService.beanOutputConverter(question);
        return hotel;
    }

    @PostMapping("/bean-output-converter2")
    public Pizza beanOutputConverter2(@RequestParam("order") String order) {
        Pizza pizza = aiService.beanOutputConverter2(order);
        return pizza;
    }

    @PostMapping("generic-bean-output-converter")
    public List<Hotel> genericBeanOutputConverter(@RequestParam("question") String question) {
        List<Hotel> list = aiService.genericBeanOutputConverter(question);
        return list;
    }

    @PostMapping("generic-bean-output-converter2")
    public List<Pizza> genericBeanOutputConverter2(@RequestParam("order") String order) {
        List<Pizza> list = aiService.genericBeanOutputConverter2(order);
        return list;
    }
}
