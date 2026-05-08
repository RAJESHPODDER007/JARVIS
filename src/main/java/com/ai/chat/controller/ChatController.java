package com.ai.chat.controller;

import com.ai.chat.model.ChatRequest;
import com.ai.chat.model.TravelPlan;
import com.ai.chat.service.AudioService;
import com.ai.chat.service.ChatService;
import com.ai.chat.service.RAG.VectorStoreService;
import com.ai.chat.service.RecipeService;
import com.ai.chat.service.TravelService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final TravelService travelService;
    private final RecipeService recipeService;
    private final ChatMemory chatMemory;
    private final VectorStoreService vectorStoreService;
    private final AudioService audioService;

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest chatRequest) {
        return chatService.chat(chatRequest.getConversationId(), chatRequest.getMessage());
    }

    @PostMapping("/chat/audio")
    public Map<String, Object> chatWithAudio(@RequestParam("file") MultipartFile file) {
        Map<String, Object> uploadResult = audioService.store(file);
        String storedFileName = (String) uploadResult.get("storedFileName");
        String transcripts = audioService.speechToText(storedFileName);
        String response = chatService.chat(null, transcripts);
        return Map.of("transcripts", transcripts, "aiResponse", response);
    }

    @GetMapping("/travel")
    public TravelPlan travelGuide(@RequestParam("city") String city,
                                  @RequestParam("days") Integer days) {
        return travelService.travelGuide(city, days);
    }

    @GetMapping("/recipe")
    public String generateRecipe(@RequestParam("dish") String dish) {
        String draft = recipeService.getRecipe(dish);
        return recipeService.refineRecipe(draft);
    }

    @GetMapping("/memory")
    public List<Message> getMessage(@RequestParam("conversationId") String conversationId) {
        return chatMemory.get(conversationId);
    }

    @GetMapping("/load")
    public String loadDocument() {
        return vectorStoreService.initialize();
    }

    @GetMapping("/weather")
    public ResponseEntity<byte[]> weather(@RequestParam("city") String city) {
        byte[] image = chatService.weatherReport(null, city);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }

    @PostMapping(value = "/chat/audio/voice", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<String> voiceChat(@RequestParam("file") MultipartFile file) {
        Map<String, Object> uploadResult = audioService.store(file);
        String storedFileName = (String) uploadResult.get("storedFileName");
        String transcript = audioService.speechToText(storedFileName);
        String aiResponse = chatService.chat(null, transcript);

        byte[] audioBytes = audioService.textToSpeech(aiResponse);
        String audioResponse = Base64.getEncoder()
                                .encodeToString(audioBytes);

        String encodedTranscript = URLEncoder.encode(transcript, StandardCharsets.UTF_8);
        String encodedAiResponse = URLEncoder.encode(aiResponse, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                    .header("Content-Type", "audio/mpeg")
                    .header("X-Transcript", encodedTranscript)
                    .header("X-AI-Response", encodedAiResponse)
                    .body(audioResponse);
    }
}
