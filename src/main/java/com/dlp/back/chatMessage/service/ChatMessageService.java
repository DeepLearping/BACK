package com.dlp.back.chatMessage.service;

import com.dlp.back.chatMessage.domain.dto.*;
import com.dlp.back.participant.repository.ParticipantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.dlp.back.chatMessage.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final RestTemplate restTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ParticipantRepository participantRepository;

    private static final String FASTAPI_CHAT_URL = "http://localhost:8000/chat";
    private static final String FASTAPI_SELECT_CHAR_URL = "http://localhost:8000/character/match";

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
                    FASTAPI_CHAT_URL,
                    HttpMethod.POST,
                    entity,
                    ChatResponseFastAPI.class
            );

            // FastAPI 응답 성공 => DB에 human & ai 메세지 저장되어 있는 상태
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                // participant 업데이트
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
        String msgImgUrl = "/" + characterId + "/" + msgImg + ".jpg";

        Long lastMessageId = chatMessageRepository.findLastInsertedIdBySessionId(chatRequest.getConversationId())
                .orElseThrow(() -> new RuntimeException("채팅방 id" + chatRequest.getConversationId() + "에 저장된 메세지가 없습니다."));

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

    public List<Map<String, Object>> findChatHistoryBySessionId(Long sessionId, int limit, int pageNo) {
        Page<Map<String, Object>> page = null;
        PageRequest pageRequest = PageRequest.of(pageNo, limit);

        page = chatMessageRepository.findChatHistoryBySessionIdWithPagination(sessionId, pageRequest);

        assert page != null;
        List<Map<String, Object>> messages = page.getContent();

        return messages;
    }

    public List<Long> selectCharacterIdFastAPI(CharacterMatchRequestFastAPI characterMatchRequestFastAPI) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<CharacterMatchRequestFastAPI> charMatchEntity = new HttpEntity<>(characterMatchRequestFastAPI, headers);

        ResponseEntity<CharacterMatchResponseFastAPI> charMatchResponseEntity = restTemplate.exchange(
                FASTAPI_SELECT_CHAR_URL,
                HttpMethod.POST,
                charMatchEntity,
                CharacterMatchResponseFastAPI.class
        );

        return charMatchResponseEntity.getBody().getSelectedCharIdList();
    }

    public List<String> getRecentChatHistoryList(Long sessionId) {
        // sessionId로 메세지 가져오기
        List<Map<String, Object>> messages = chatMessageRepository.findChatMessagesBySessionIdOrderByIdDesc(sessionId);

        List<String> chatHistoryList = new ArrayList<>();
        List<String> currentSet = new ArrayList<>();
        int setCount = 0;

        for (Map<String, Object> message : messages) {
            if ("user".equals(message.get("role"))) {
                // 유저 메세지이면 새로운 set부터 시작
                if (!currentSet.isEmpty()) {
                    chatHistoryList.addAll(currentSet);
                    currentSet = new ArrayList<>();
                    setCount++;
                }

                String content = getContentFromJsonMessage((String) message.get("message"));
                content = "human: " + content;
                currentSet.add(content);

                if (setCount >= 5) {
                    chatHistoryList.addAll(currentSet);
                    break;
                }
            } else if ("ai".equals(message.get("role"))) {
                String content = getContentFromJsonMessage((String) message.get("message"));
                currentSet.add(content);
            }
        }

        // 히스토리가 5쌍보다 적을 때 마지막 set 더해주기
        if (!currentSet.isEmpty() && setCount < 5) {
            chatHistoryList.addAll(currentSet);
        }

        // 채팅 순서에 맞게 reverse
        Collections.reverse(chatHistoryList);
        return chatHistoryList;
    }

    public String getContentFromJsonMessage(String jsonMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageMap = null;
        String content = jsonMessage;

        try {
            messageMap = objectMapper.readValue(jsonMessage, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> dataMap = (Map<String, Object>) messageMap.get("data");

        if (dataMap != null && dataMap.containsKey("content")) {
            content = (String) dataMap.get("content");
        }

        return content;
    }

    @Transactional
    public int deleteHumanQuestions(DeleteUserMessageRequest deleteUserMessageRequest) {
        // deleteUserMessageRequest.getNumToBeDeleted()개 만큼 최신 human message 삭제
        Long conversationId = deleteUserMessageRequest.getConversationId();
        int numToBeDeleted = deleteUserMessageRequest.getNumToBeDeleted();

        // {human:ai}쌍이므로 *2개의 최신 메세지 가져오기
        int fetchLimit = numToBeDeleted * 2;
        List<Map<String, Object>> messages = chatMessageRepository.findLimitedChatMessagesBySessionIdOrderByIdDesc(conversationId, fetchLimit);

        int count = 0;
        for (Map<String, Object> message : messages) {
            // operation time을 줄이기 위해 실제 중복되는 메세지인지는 확인하지 않음
            if ("user".equals(message.get("role"))) {
                chatMessageRepository.deleteMessagesById((Long) message.get("id"));
                count++;
                if (count >= numToBeDeleted) break;
            }
        }

        return count;
    }
}
