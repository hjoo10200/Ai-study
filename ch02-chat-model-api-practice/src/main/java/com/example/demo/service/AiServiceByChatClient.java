package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiServiceByChatClient {
    private final ChatClient chatClient;

    public AiServiceByChatClient(ChatClient.Builder chatBuilder) {
        this.chatClient = chatBuilder
                            .defaultSystem("당신은 모든걸 자바 코드 알고리즘으로 설계하는 개발자입니다.")
                            .defaultOptions(OpenAiChatOptions.builder()
                                        .model("gpt-6.1-sol")
                                        // .temperature(1.0)
                                        .reasoningEffort("none")
                                        .build())            
                            .build();
    }

    public String chat1(String question) {

        String answer = chatClient.prompt()
                                //   .system("당신은 개발자입니다.")
                                  .user(question)
                                //   .options(OpenAiChatOptions.builder()
                                //         // .model("gpt-6.1-sol")
                                //         // .temperature(1.0)
                                //         .reasoningEffort("none")
                                //         .build())
                                   .call()                         
                                   .content();
        return answer;
     }

    public Flux<String> chat2(String question) {
        Flux<String> answer = chatClient.prompt()
                                //   .system("당신은 개발자입니다.")
                                  .user(question)
                                //   .options(OpenAiChatOptions.builder()
                                //         // .model("gpt-6.1-sol")
                                //         // .temperature(1.0)
                                //         .reasoningEffort("none")
                                //         .build())
                                   .stream()                         
                                   .content();
        return answer;
    }
}
