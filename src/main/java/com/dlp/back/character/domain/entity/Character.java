package com.dlp.back.character.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "`character`")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long charNo;
    private String description;
    private String charName;
    private String profileImage;
    private String role;

    @ColumnDefault("0")
    private int chatCount; // 캐릭터 선택 횟수 표시(이득규)

}

