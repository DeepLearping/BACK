package com.dlp.back.chatRoom.domain.dto;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.chatMessage.domain.entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatRoomResponse2 {

    private Long sessionId;
    private String roomName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Member member;
    private List<Character> characters;
    private List<ChatMessage> lastChatMessage;
}
