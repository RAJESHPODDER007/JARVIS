package com.ai.chat.service;

import com.ai.chat.model.TravelPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final ChatClient chatClient;

    @Value("classpath:prompts/travel-guide.st")
    private Resource travelGuideTemplate;

    public TravelPlan travelGuide(String city, Integer days){
        PromptTemplate template = new PromptTemplate(travelGuideTemplate);
        Map<String,Object> params = Map.of(
                "city",city,
                "days",days
        );

        Prompt prompt = template.create(params);
        return chatClient.prompt(prompt)
                .call().entity(TravelPlan.class);
    }
}
