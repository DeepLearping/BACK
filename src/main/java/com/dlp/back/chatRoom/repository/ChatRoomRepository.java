package com.dlp.back.chatRoom.repository;

import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.participant p1 JOIN cr.participant p2 " +
            "WHERE p1.member.memberNo = :memberNo AND p2.character.charNo = :charNo")
    List<ChatRoom> findChatRoomsByCharNoAndMemberNo(Long charNo, Long memberNo);
}
