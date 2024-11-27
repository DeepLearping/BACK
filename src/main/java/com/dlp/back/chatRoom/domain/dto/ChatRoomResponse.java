package com.dlp.back.chatRoom.domain.dto;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.member.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatRoomResponse {

    private Long sessionId;
    private String roomName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Member member;
    private Character character;

}
