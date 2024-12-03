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
public class CharacterMatchRequestFastAPI {
    private String question;

    @JsonProperty("char_id_list")
    private List<Long> charIdList;

}
