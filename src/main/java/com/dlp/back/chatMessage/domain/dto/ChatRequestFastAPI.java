package com.dlp.back.chatMessage.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatRequestFastAPI {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("conversation_id")
    private Long conversationId;

    private String question;

    @JsonProperty("character_id")
    private Long characterId;
}
