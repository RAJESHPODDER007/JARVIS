package com.ai.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final ChatClient chatClient;

    public String getRecipe(String dish){
        Prompt prompt = new Prompt(
                new UserMessage("Write a recipe for a " + dish+
                        ". Include ingredients and preparation steps")
        );
        return chatClient.prompt(prompt)
                .call().content();
    }


    public String refineRecipe(String draft){
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("You are a recipe formatter. Convert recipe into JSON with keys: " +
                        "'dish', 'ingredients', 'steps'"),
                new UserMessage("Here is the recipe"+ draft))
        );
        return chatClient.prompt(prompt)
                .call().content();
    }
}
