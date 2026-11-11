package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiService {

    public final ChatClient chatClient;

    public AiService(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder
                .defaultSystem("")
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-6.1-sol")
                        .reasoningEffort("low")
                        .build())
                .build();
    }

    public String chat1(String question) {
        String answer = chatClient.prompt()
                .user(question)
                .call()
                .content();
        return answer;
    }

    public Flux<String> chat2(String question) {
        Flux<String> answer = chatClient.prompt()
                .user(question)
                .stream()
                .content();

        return answer;

    }
}
