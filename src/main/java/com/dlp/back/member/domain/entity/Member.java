package com.dlp.back.member.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNo;

    private String googleId;
    private String kakaoId;
    private String name;
    private String nickname;
    private String email;
    private String picture;
    private String role;

}

