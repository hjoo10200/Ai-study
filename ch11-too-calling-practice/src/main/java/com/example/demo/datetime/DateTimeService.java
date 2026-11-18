package com.example.demo.datetime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service 
public class DateTimeService {

    private final ChatClient chatClient;
    private final DateTimeTools dateTimeTools;
    

    public DateTimeService(ChatClient.Builder chatClientbBuilder,DateTimeTools dateTimeTools){
        this.chatClient = chatClientbBuilder.build();
        this.dateTimeTools = dateTimeTools;
        
    }

    public String chat(String question) {
        String answer = chatClient.prompt()
        .user(question)
        .tools(dateTimeTools)
        .call()
        .content();
        

        return answer;


    }
}
