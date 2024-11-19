package com.dlp.back.chatRoom.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateChatRoomDTO {

    private String roomName;
    private String description;
    private LocalDateTime lastModifiedDate;

}
