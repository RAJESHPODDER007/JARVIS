package com.ai.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel){
        return ChatClient
                .builder(openAiChatModel)
                .build();
    }
}
