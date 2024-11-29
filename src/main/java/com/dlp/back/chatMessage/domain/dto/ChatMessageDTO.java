package com.dlp.back.chatMessage.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatMessageDTO {
    private Long messageNo;
    private String content;
    private LocalDateTime createdDate;
    private String msgImgUrl;
}
