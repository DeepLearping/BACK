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
    private Long conversationId;

    private int numToBeDeleted;
}
