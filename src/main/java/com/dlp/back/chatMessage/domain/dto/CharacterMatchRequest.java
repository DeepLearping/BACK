package com.dlp.back.chatMessage.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CharacterMatchRequest {
    private String question;

//    @JsonProperty("conversation_id")
    private Long conversationId;

//    @JsonProperty("char_id_list")
    private List<Long> charIdList;
}
