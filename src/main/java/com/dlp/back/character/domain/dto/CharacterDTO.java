package com.dlp.back.character.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CharacterDTO {
    private Long charNo;
    private String description;
    private String charName;
    private String profileImage;
    private String role;

}
