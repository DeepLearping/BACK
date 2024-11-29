package com.dlp.back.chatMessage.service;

import com.dlp.back.chatMessage.domain.dto.ChatRequest;
import com.dlp.back.chatMessage.domain.dto.ChatRequestFastAPI;
import com.dlp.back.chatMessage.domain.dto.ChatResponseFastAPI;
import com.dlp.back.chatMessage.domain.dto.MsgImgRequest;
import com.dlp.back.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.dlp.back.chatMessage.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final RestTemplate restTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ParticipantRepository participantRepository;

    private static final String FASTAPI_URL = "http://localhost:8000/chat";

    public Map<String,Object> sendQuestionToFastAPI(ChatRequest chatRequest) {
        try {
            // FastAPI에 보낼 payload
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // FastAPI에 보낼 request body
            ChatRequestFastAPI fastApiRequest = new ChatRequestFastAPI(
                    chatRequest.getUserId(),
                    chatRequest.getConversationId(),
                    chatRequest.getQuestion(),
                    chatRequest.getCharacterId()
            );

            HttpEntity<ChatRequestFastAPI> entity = new HttpEntity<>(fastApiRequest, headers);

            // FastAPI 서버 포스트 요청
            ResponseEntity<ChatResponseFastAPI> responseEntity = restTemplate.exchange(
                    FASTAPI_URL,
                    HttpMethod.POST,
                    entity,
                    ChatResponseFastAPI.class
            );

            // FastAPI 응답 성공 => DB에 human & ai 메세지 저장되어 있는 상태
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // participantNo 업데이트
                updateChatMessageParticipant(chatRequest);

                Map<String,Object> map = new HashMap<>();
                map.put("answer", responseEntity.getBody().getAnswer());
                map.put("characterId", responseEntity.getBody().getCharacterId());
                map.put("msgImg", responseEntity.getBody().getMsgImg());

                if(responseEntity.getBody().getMsgImg() > 0){
                    updateMsgImgUrl(responseEntity.getBody().getCharacterId(), responseEntity.getBody().getMsgImg(), chatRequest);
                }

                return map;
            } else {
                log.error("FastAPI 서버 오류 발생: {}", responseEntity.getStatusCode());
                throw new RuntimeException("FastAPI 서버 오류 발생");
            }
        } catch (Exception e) {
            log.error("FastAPI 호출 오류", e);
            throw new RuntimeException("FastAPI 호출 오류", e);
        }
    }

    private void updateChatMessageParticipant(ChatRequest chatRequest) {
        participantRepository.findBySessionIdAndCharacterId(
                chatRequest.getConversationId(),
                chatRequest.getCharacterId()
        ).ifPresent(participant -> {
            // 해당 채팅방에서 마지막으로 insert한 메세지 id 가져오기 (= 마지막으로 insert한 ai 메세지)
            Long lastMessageId = chatMessageRepository.findLastInsertedIdBySessionId(chatRequest.getConversationId())
                    .orElseThrow(() -> new RuntimeException("채팅방 id" + chatRequest.getConversationId() + "에 저장된 메세지가 없습니다."));

            int rowsUpdated = chatMessageRepository.updateParticipantNo(lastMessageId, participant);

            if (rowsUpdated == 0) {
                throw new RuntimeException("채팅방 id" + chatRequest.getConversationId() + "에 저장된 메세지가 없습니다.");
            }
        });
    }

    private void updateMsgImgUrl(long characterId, int msgImg, ChatRequest chatRequest) {
        // Generate the msgImgUrl string
        String msgImgUrl = "/" + characterId + "/" + msgImg + ".jpg";

        // Find the last inserted message ID
        Long lastMessageId = chatMessageRepository.findLastInsertedIdBySessionId(chatRequest.getConversationId())
                .orElseThrow(() -> new RuntimeException("채팅방 id" + chatRequest.getConversationId() + "에 저장된 메세지가 없습니다."));

        // Update the msgImgUrl column for the last message
        int rowsUpdated = chatMessageRepository.updateMsgImgUrl(lastMessageId, msgImgUrl);

        if (rowsUpdated == 0) {
            throw new RuntimeException("Failed to update msgImgUrl for message ID: " + lastMessageId);
        }
    }

    public Resource loadMsgImage(String characterId, String msgImg) throws Exception {
        Path basePath = Paths.get("src/main/resources/static/image/msgImg/" + characterId);
        String imageName = msgImg;
        Path imagePath = basePath.resolve(imageName);

        Resource image = new UrlResource(imagePath.toUri());
        if (image.exists() || image.isReadable()) {
            return image;
        } else {
            throw new Exception("이미지를 찾을 수 없음.");
        }
    }
}
