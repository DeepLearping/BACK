package com.dlp.back.chatMessage.controller;

import com.dlp.back.chatMessage.domain.dto.ChatRequest;
import com.dlp.back.chatMessage.domain.dto.MsgImgRequest;
import com.dlp.back.chatMessage.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
}