package com.dlp.back.chatMessage.controller;

import com.dlp.back.chatMessage.domain.dto.ChatRequest;
import com.dlp.back.chatMessage.domain.dto.MsgImgRequest;
import com.dlp.back.chatMessage.domain.entity.ChatMessage;
import com.dlp.back.chatMessage.service.ChatMessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "ChatMessage")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chatMessage")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/sendQuestion")
    public ResponseEntity<Map<String,Object>> sendQuestionToFastAPI(@RequestBody ChatRequest chatRequest) {
        try {
            Map<String,Object> responseAnswer = chatMessageService.sendQuestionToFastAPI(chatRequest);

            return ResponseEntity.ok(responseAnswer);
        } catch (Exception e) {
            Map<String,Object> error = new HashMap<>();
            error.put("message", "내부 서버 오류 발생: " + e.getMessage());
            log.error("내부 서버 오류 발생", e);
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/getMsgImg/{characterId}/{msgImg}")
    public ResponseEntity<Resource> getMsgImg(@PathVariable String characterId, @PathVariable String msgImg) {
        try {
            Resource image = chatMessageService.loadMsgImage(characterId,msgImg);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_JPEG) // 이미지 타입
                    .body(image);
        } catch (Exception e) {
            log.error("이미지 로드 오류: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // TODO: 일정량의 최신 채팅 히스토리만 가져오고 나머지 히스토리는 무한스크롤로 로딩
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<Map<String, Object>>> getChatHistory(@PathVariable Long sessionId) {
        try {
            List<Map<String, Object>> messages = chatMessageService.findChatHistoryBySessionId(sessionId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
}