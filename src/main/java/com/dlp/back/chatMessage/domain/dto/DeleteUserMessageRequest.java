package com.dlp.back.chatMessage.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DeleteUserMessageRequest {
    @JsonProperty("conversation_id")
    private Long conversationId;

    @JsonProperty("num_to_be_deleted")
    private int numToBeDeleted;
}
