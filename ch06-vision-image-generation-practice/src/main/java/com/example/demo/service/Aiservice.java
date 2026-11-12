package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class Aiservice {

    private final ChatClient chatClient;

    public Aiservice(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-6.1-terra")
                        .reasoningEffort("none")
                        .temperature(1.0)
                        .build())
                .build();
    }

    public Flux<String> imageAnalysis(String question, String contentType, byte[] bytes) {

        // SystemMessage 객체 생성
        SystemMessage systemMessage = SystemMessage.builder()
                .text("""
                        당신은 이미지 분석 전문가입니다.
                        사용자의 질문에 맞게 이미지를 분석하고 답변을 한국어로 하세요.


                                    """).build();

        // 이미지를 Media 객체 생성
        Media media = Media.builder()
                .mimeType(MimeType.valueOf(contentType)) // 파일타입 문자열을 MimeType 객체로 변환
                .data(new ByteArrayResource(bytes)) // byte 배열을 data 객체로 변환
                .build();

        // 멀티 모달리티로 입력
        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .media(media)
                .build();

        // LLM을 호출해서 답변받기
        Flux<String> answer = chatClient.prompt()
                .messages(systemMessage, userMessage)
                .stream()
                .content();

        return answer;
    }

}
