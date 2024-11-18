package com.dlp.back.character.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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

}

