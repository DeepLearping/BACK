package com.dlp.back.chatRoom.repository;

import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN cr.participant p " +
            "WHERE (p.member.memberNo = :memberNo AND p.character IS NULL) " +
            "OR (p.character.charNo = :charNo AND p.member IS NULL) " +
            "GROUP BY cr " +
            "HAVING COUNT(p) = 2")
    List<ChatRoom> findChatRoomsByCharNoAndMemberNo(Long charNo, Long memberNo);

    ChatRoom findChatRoomByRoomName(String charName);
}
