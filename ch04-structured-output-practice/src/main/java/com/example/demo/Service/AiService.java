package com.example.demo.Service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Hotel;
import com.example.demo.dto.Pizza;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiService {
    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<String> listOutputConverter(String question) {
        List<String> list = chatClient.prompt()
                .user(question)
                .call()
                // 프롬프트에 출력 형식 지시를 추가하고 모델 응답을 List<String>으로 변환한다.
                .entity(new ListOutputConverter());

        return list;
    }

    public Hotel beanOutputConverter(String question) {
        Hotel hotel = chatClient.prompt()
                .user(question)
                .call()
                .entity(Hotel.class);

        return hotel;
    }

    public Pizza beanOutputConverter2(String order) {
        Pizza pizza = chatClient.prompt()
                .user(order)
                .call()
                .entity(Pizza.class);

        return pizza;
    }

    public List<Hotel> genericBeanOutputConverter(String question) {
        List<Hotel> list = chatClient.prompt()
                .user(question)
                .call()
                .entity(new ParameterizedTypeReference<List<Hotel>>() {

                });
        return list;
    }

    public List<Pizza> genericBeanOutputConverter2(String question) {
        List<Pizza> list = chatClient.prompt()
                .user(question)
                .call()
                .entity(new ParameterizedTypeReference<List<Pizza>>() {

                });
        return list;
    }

}
