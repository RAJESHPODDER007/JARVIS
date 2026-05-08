package com.ai.chat.service;

import com.openai.models.audio.AudioResponseFormat;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioService{
    private static final Path AUDIO_DIR= Path.of(System.getProperty("java.io.tmpdir"), "spring-ai-audio");
    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final OpenAiAudioSpeechModel speechModel;

    public byte[] textToSpeech(String text){
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .model("tts-1") // tts-1 & tts-1-hd
                .voice("alloy") // alloy echo fable onyx nova shimmer
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text,options);
        TextToSpeechResponse response = speechModel.call(prompt);
        return response.getResult().getOutput();
    }

    public String speechToText(String storedFileName){
        try{
            Path audioPath = AUDIO_DIR.resolve(storedFileName);
            byte[] audioBytes = Files.readAllBytes(audioPath);
            Resource audio = new ByteArrayResource(audioBytes){
                @Override
                public @Nullable String getFilename() {
                    return storedFileName;
                }
            };

            AudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                    .model("whisper-1")
                    .responseFormat(AudioResponseFormat.JSON)
                    .build();

            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audio,options);

            AudioTranscriptionResponse response = transcriptionModel.call(prompt);
            return response.getResult().getOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String,Object> store(MultipartFile file){
        try{
            Files.createDirectories(AUDIO_DIR);
            String fileId = UUID.randomUUID().toString();
            String storedFileName=fileId+"_"+file.getOriginalFilename();
            Path targetPath = AUDIO_DIR.resolve(storedFileName);
            Files.copy(file.getInputStream(),targetPath);
            Map<String,Object> response = new HashMap<>();
            response.put("fileId",fileId);
            response.put("originalFileName",file.getOriginalFilename());
            response.put("storedFileName",storedFileName);
            response.put("contentType",file.getContentType());
            response.put("size",file.getSize());
            return response;
        }catch (IOException e){
            throw  new RuntimeException("Failed to store audio file", e);
        }
    }
}
