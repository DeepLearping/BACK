package com.dlp.back.chatMessage.repository;

import com.dlp.back.chatMessage.domain.entity.ChatMessage;
import com.dlp.back.participant.domain.entity.Participant;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage c SET c.msgImgUrl = :msgImgUrl WHERE c.id = :id")
    int updateMsgImgUrl(@Param("id") Long id, @Param("msgImgUrl") String msgImgUrl);

    @Query("SELECT " +
            "CASE WHEN c.participant.character.charNo IS NULL THEN 'user' ELSE 'ai' END AS role, " +
            "c.message AS message, " +
            "c.msgImgUrl AS msgImgUrl, " +
            "c.participant.character.charNo AS characterId " +
            "FROM ChatMessage c " +
            "LEFT JOIN c.participant.character pc " +
            "WHERE c.chatRoom.sessionId = :sessionId " +
            "ORDER BY c.id ASC")
    List<Map<String, Object>> findChatHistoryBySessionId(@Param("sessionId") Long sessionId);
}
