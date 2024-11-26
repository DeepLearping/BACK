package com.dlp.back.chatRoom.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatRoomInfo {

    private Long charNo;
    private String charName;
    private Long memberNo;
}
