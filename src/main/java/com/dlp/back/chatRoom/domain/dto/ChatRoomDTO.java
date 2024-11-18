package com.dlp.back.chatRoom.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatRoomDTO {
    private Long roomNo;
    private String roomName;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
