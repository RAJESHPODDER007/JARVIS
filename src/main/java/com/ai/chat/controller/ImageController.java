package com.ai.chat.controller;

import com.ai.chat.model.ChatRequest;
import com.ai.chat.service.ImageCaptionService;
import com.ai.chat.service.ImageGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/image")
@RequiredArgsConstructor
public class ImageController {

    public final ImageCaptionService imageCaptionService;
    public final ImageGenerationService imageGenerationService;

    @PostMapping("/caption")
    public String caption(@RequestBody ChatRequest chatRequest){
        return imageCaptionService.captionImage(chatRequest.getImageName(), chatRequest.getMessage());
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generate(String message){
        byte[] image =  imageGenerationService.generate(message);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

}
