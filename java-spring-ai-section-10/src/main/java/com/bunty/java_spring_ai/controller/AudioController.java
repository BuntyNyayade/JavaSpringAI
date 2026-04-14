package com.bunty.java_spring_ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.TextStyle;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
    private final TextToSpeechModel  textToSpeechModel;

    public AudioController(OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel, TextToSpeechModel textToSpeechModel) {
        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
        this.textToSpeechModel = textToSpeechModel;
    }

    @GetMapping("/transcribe")
    public String transcribe(@Value("classpath:SpringAI.mp3") Resource resource) {
       return  openAiAudioTranscriptionModel.call(resource);
    }

    @GetMapping("/transcribe-options")
    public String transcribeOptions(@Value("classpath:SpringAI.mp3") Resource resource) {
        AudioTranscriptionResponse call = openAiAudioTranscriptionModel.call(new AudioTranscriptionPrompt(resource,
                OpenAiAudioTranscriptionOptions.builder()
                        .language("en")
                        .prompt("My name")
                        .temperature(0.5f)
                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.VTT)
                        .build()
        ));

        return call.getResult().getOutput();
    }

    @GetMapping("/speech")
    public String speech(@RequestParam String text) throws IOException {
        byte[] call = textToSpeechModel.call(text);
        Path path = Paths.get("output.mp3");
        Files.write(path, call);
        return "MP3 saved successfully";
    }

    @GetMapping("/speech-options")
    public String speechOptions(@RequestParam String text) throws IOException {
        TextToSpeechResponse call = textToSpeechModel.call(new TextToSpeechPrompt(text,
                OpenAiAudioSpeechOptions.builder()
                        .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                        .speed(2.0).responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                        .build()
        ));

        Path path = Paths.get("speech-output.mp3");
        Files.write(path, call.getResult().getOutput());
        return "MP3 saved successfully";
    }
}
