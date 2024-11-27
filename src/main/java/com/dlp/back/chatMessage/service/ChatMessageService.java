package com.dlp.back.chatMessage.service;

import com.dlp.back.chatMessage.domain.dto.ChatRequest;
import com.dlp.back.chatMessage.domain.dto.ChatRequestFastAPI;
import com.dlp.back.chatMessage.domain.dto.ChatResponseFastAPI;
import com.dlp.back.chatMessage.domain.entity.ChatMessage;
import com.dlp.back.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.dlp.back.chatMessage.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final RestTemplate restTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ParticipantRepository participantRepository;

    private static final String FASTAPI_URL = "http://localhost:8000/chat";

    public String sendQuestionToFastAPI(ChatRequest chatRequest) {
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

                // TODO: 감정 imageUrl 저장

                return responseEntity.getBody().getAnswer();
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
}
