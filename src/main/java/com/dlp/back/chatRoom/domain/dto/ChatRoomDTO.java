package com.dlp.back.chatRoom.domain.dto;

import com.dlp.back.participant.domain.entity.Participant;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatRoomDTO {
    private Long sessionId;
    private String roomName;
    private String description;
    private List<Participant> participant;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
