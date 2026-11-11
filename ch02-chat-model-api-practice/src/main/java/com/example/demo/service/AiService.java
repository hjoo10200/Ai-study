package com.example.demo.service;

import javax.sound.midi.SysexMessage;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiService {

    public AiService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private final ChatModel chatModel;

    public String chat1(String question) {

        // Message 생성
        SystemMessage systemMessage = SystemMessage.builder()
                .text("교회 라틴어로 답변하라")
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .build();

        // 동기 방식
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-6.1-sol")
                .temperature(1.0)
                .reasoningEffort("low")
                .build();

        // ChatOptions chatOptions = ChatOptions.builder()
        //                                      .temperature(1.0)
        //                                      .build();

        Prompt prompt = Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(chatOptions)
                .build();

        String answer = chatModel.call(prompt)
        .getResult()
        .getOutput()
        .getText();

        // ChatResponse response = chatModel.call(prompt);
        // Generation generation = response.getResult();
        // AssistantMessage assistantMessage = generation.getOutput();
        // String answer = assistantMessage.getText();


        return answer;
    }

    // public String chat1(String question) {
    // // 동기 방식
    // OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
    // .temperature(1.0)
    // .reasoningEffort("low")
    // .build();
    // // Prompt prompt = new Prompt(question, chatOptions);
    // Prompt prompt =Prompt.builder()
    // .content(question)
    // .chatOptions(chatOptions)
    // .build();

    // String answer = chatModel.call(prompt)
    // .getResult()
    // .getOutput()
    // .getText();
    // return answer;
    // }

    // public String chat1(String question) {
    // // 동기 방식
    // String answer = chatModel.call(question);
    // return answer;
    // }

    // -------------------------------------------------------------------------------------------------------
    // public Flux<String> chat2(String question) {

    // Flux<String> answer = chatModel.stream(question);
    // return answer;

    // 스트리밍 방식
    public Flux<String> chat2(String question) {

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .temperature(1.0)
                .reasoningEffort("none")
                .build();
        Prompt prompt = Prompt.builder()
                .content(question)
                .chatOptions(chatOptions)
                .build();
        Flux<ChatResponse> fluxChatResponse = chatModel.stream(prompt);

        Flux<String> answer = fluxChatResponse.map(chatResponse -> {
            if (chatResponse.getResult() != null) {
                AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
                String chunk = assistantMessage.getText();
                if (chunk == null)
                    chunk = "";
                return chunk;
            } else {
                return "";
            }
        });
        return answer;
    }

}
