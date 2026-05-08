package com.ai.chat.service;

import com.ai.chat.service.RAG.VectorStoreService;
import com.ai.chat.tools.ContactTool;
import com.ai.chat.tools.QuickNotesTool;
import com.ai.chat.tools.WeatherTool;
import com.ai.chat.tools.WebSearchTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final ImageModel imageModel;
    private final ChatMemory chatMemory;
    private final WeatherTool weatherTools;
    private final ContactTool contactTool;
    private final VectorStoreService vectorStoreService;
    private final QuickNotesTool notesTool;
    private final WebSearchTool webSearchTool;

    @Value("classpath:prompts/weather-prompt.st")
    private Resource weatherReportPrompt;

    private final String SYSTEM_PROMPT= """
            You are a helpful assistance, your task is to answer the user's query.
            If you know the answer then your answer should be simple and should be address user's query without description.
            If you do not know the answer the use tool ('WeatherTool', 'QuickNotesTool','WebSearchTool'), if those tools give any answer then minimised the descriptive answer to simpler answer.
            Your response should be only in English language. No other language preferred""";

    private final String ASSISTANT_MESSAGE= """
            I don't know
            """;

    public String chat(String conversationId,String message){
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String convId= (conversationId==null || conversationId.isBlank())
                ? UUID.randomUUID().toString() : conversationId;

        Prompt prompt = new Prompt(List.of(
                new SystemMessage("Today's date is " + today + " ."+
                            SYSTEM_PROMPT),
                new UserMessage(message)
        ));
        //QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStoreService.getVectorStore()).build();
        return chatClient
                .prompt(prompt)
                .advisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .conversationId(convId)
                        .build())
                       // , questionAnswerAdvisor)
                .tools(weatherTools,notesTool, webSearchTool)
                        //,contactTool)
                .call()
                .content();
    }

    public byte[] weatherReport(String conversationId,String city){
        log.info("Started template generation");
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        PromptTemplate template = new PromptTemplate(weatherReportPrompt);
        Map<String,Object> params = Map.of(
                "city",city
        );

        Prompt prompt = template.create(params);
        prompt.augmentSystemMessage("Today's date is " + today + " ."+
                SYSTEM_PROMPT);
        String weatherAssistantMessage= chatClient
                .prompt(prompt)
                .tools(weatherTools)
                .call()
                .content();
        String imagePrompt="""
                Your role is to generate image based on the input.
                create a weather image, image may contain 'sky','cloud','sun','rain','thunderstorm','moon' 
                """ + weatherAssistantMessage;
        log.info("Template {}", imagePrompt);
        log.info("Generating image");

        ImagePrompt iPrompt = new ImagePrompt(imagePrompt, OpenAiImageOptions.builder()
                .responseFormat("b64_json")
                .width(1792)
                .height(1024)
                .build());
        ImageResponse imageResponse = imageModel.call(iPrompt);
        String b64 = imageResponse.getResult().getOutput().getB64Json();
        log.info("Image generated");
        return Base64.getDecoder().decode(b64);
    }
}
