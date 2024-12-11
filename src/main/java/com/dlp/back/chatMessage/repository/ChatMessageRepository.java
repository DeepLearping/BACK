package com.dlp.back.chatMessage.repository;

import com.dlp.back.chatMessage.domain.dto.ChatMessageDTO;
import com.dlp.back.chatMessage.domain.entity.ChatMessage;
import com.dlp.back.participant.domain.entity.Participant;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.participant = :participant WHERE c.id = :id")
    int updateParticipantNo(@Param("id") Long id, @Param("participant") Participant participant);

    @Query("SELECT c.id FROM ChatMessage c WHERE c.chatRoom.sessionId = :sessionId ORDER BY c.id DESC LIMIT 1")
    Optional<Long> findLastInsertedIdBySessionId(@Param("sessionId") Long sessionId);

    ChatMessage getChatMessageById(Long sessionId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.msgImgUrl = :msgImgUrl WHERE c.id = :id")
    int updateMsgImgUrl(@Param("id") Long id, @Param("msgImgUrl") String msgImgUrl);

    @Query("SELECT " +
            "CASE WHEN c.participant.character.charNo IS NULL THEN 'user' ELSE 'ai' END AS role, " +
            "c.message AS message, " +
            "c.msgImgUrl AS msgImgUrl, " +
            "c.participant.character.charNo AS characterId, " +
            "c.id AS id, " +
            "c.createdDate AS createdDate " +
            "FROM ChatMessage c " +
            "LEFT JOIN c.participant.character pc " +
            "WHERE c.chatRoom.sessionId = :sessionId " +
            "AND (:lastFetchedId IS NULL OR c.id < :lastFetchedId) " +
            "ORDER BY c.id DESC LIMIT :limit")
    List<Map<String, Object>> findChatHistoryBySessionIdWithPagination(
            Long sessionId,
            int limit, Long lastFetchedId);

    @Query("SELECT " +
            "CASE WHEN c.participant.character.charNo IS NULL THEN 'user' ELSE 'ai' END AS role, " +
            "c.message AS message " +
            "FROM ChatMessage c " +
            "LEFT JOIN c.participant.character pc " +
            "WHERE c.chatRoom.sessionId = :sessionId " +
            "ORDER BY c.id DESC")
    List<Map<String, Object>> findChatMessagesBySessionIdOrderByIdDesc(@Param("sessionId") Long sessionId);

    @Query("SELECT " +
            "CASE WHEN c.participant.character.charNo IS NULL THEN 'user' ELSE 'ai' END AS role, " +
            "c.message AS message, " +
            "c.id AS id " +
            "FROM ChatMessage c " +
            "LEFT JOIN c.participant.character pc " +
            "WHERE c.chatRoom.sessionId = :sessionId " +
            "ORDER BY c.id DESC LIMIT :limit")
    List<Map<String, Object>> findLimitedChatMessagesBySessionIdOrderByIdDesc(@Param("sessionId") Long sessionId, @Param("limit") int limit);

    @Modifying
    @Query("DELETE FROM ChatMessage WHERE id = :id")
    void deleteMessagesById(@Param("id") Long id);

    // chatMessage 내림차순 정리
    @Query("SELECT c from ChatMessage c WHERE c.chatRoom.sessionId = :sessionId ORDER BY  c.id DESC")
    List<ChatMessage> findTopByChatRoomSessionIdOrderByCreatedDateDesc(@Param("sessionId") Long sessionId);
}
