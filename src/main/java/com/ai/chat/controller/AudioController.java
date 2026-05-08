package com.ai.chat.controller;

import com.ai.chat.service.AudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ai/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String,Object>> uploadAudio(@RequestParam("file")MultipartFile file){
        Map<String,Object> response= audioService.store(file);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/to-text")
    public ResponseEntity<Map<String,Object>> speechToText(@RequestParam("file")MultipartFile file) {
        Map<String,Object> uploadResult= audioService.store(file);
        String storedFileName= (String)uploadResult.get("storedFileName");
        String text = audioService.speechToText(storedFileName);
        return ResponseEntity.ok(
                Map.of("text",text)
        );
    }

    @PostMapping("/to-speech")
    public ResponseEntity<byte[]> toSpeech(@RequestParam("text") String text){
        byte[] audio = audioService.textToSpeech(text);
        return ResponseEntity.ok()
                .header("content-type", "audio/mpeg")
                .body(audio);
    }

}
