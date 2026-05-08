package com.ai.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@RequiredArgsConstructor
public class ImageCaptionService {
    private final ChatClient chatClient;

    public String captionImage(String imageName, String message){
        Resource resource = new ClassPathResource("images/"+imageName);
        return chatClient.prompt()
                .user(userSpec-> userSpec
                                .text(message)
                                .media(MimeTypeUtils.IMAGE_JPEG,resource))
                .call()
                .content();
    }
}
