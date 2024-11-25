package com.dlp.back.member.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UpdateMemberDTO {

    private Long memberNo;
    private String nickname;
}
