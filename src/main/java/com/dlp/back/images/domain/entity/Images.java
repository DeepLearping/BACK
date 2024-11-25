package com.dlp.back.images.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageNo;

    private String imageUrl;
    private String imageOrigName;
    private String imageSavedName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
