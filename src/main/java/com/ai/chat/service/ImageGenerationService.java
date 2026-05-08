package com.ai.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ImageGenerationService {

    private final ImageModel imageModel;

    public byte[] generate(String message){
        ImagePrompt prompt = new ImagePrompt(message, OpenAiImageOptions.builder()
                .responseFormat("b64_json")
                //.width()
                //.height()
                .build());
        ImageResponse imageResponse = imageModel.call(prompt);
        String b64 = imageResponse.getResult().getOutput().getB64Json();
        return Base64.getDecoder().decode(b64);

    }
}
