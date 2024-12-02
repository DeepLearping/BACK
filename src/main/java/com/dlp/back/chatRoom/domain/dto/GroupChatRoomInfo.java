package com.dlp.back.chatRoom.domain.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GroupChatRoomInfo {
    private List<Long> charNo;
    private String groupName;
    private String groupDescription;
    private Long memberNo;
}
