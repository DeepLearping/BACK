package com.dlp.back.chatMessage.controller;

import com.dlp.back.chatMessage.domain.dto.ChatRequest;
import com.dlp.back.chatMessage.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatMessage")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chatMessage")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/sendQuestion")
    public ResponseEntity<String> sendQuestionToFastAPI(@RequestBody ChatRequest chatRequest) {
        try {
            String responseAnswer = chatMessageService.sendQuestionToFastAPI(chatRequest);

            return ResponseEntity.ok(responseAnswer);
        } catch (Exception e) {
            log.error("내부 서버 오류 발생", e);
            return ResponseEntity.status(500).body("내부 서버 오류 발생");
        }
    }
}