package com.dlp.back.chatMessage.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MsgImgRequest {
    @JsonProperty("character_id")
    private Long characterId;

    @JsonProperty("msg_img")
    private int msgImg;
}
