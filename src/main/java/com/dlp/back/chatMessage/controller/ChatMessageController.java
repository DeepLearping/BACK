package com.dlp.back.chatMessage.controller;

import com.dlp.back.chatMessage.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatMessage")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chatMessage")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    
}