package com.dlp.back.images.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ImagesDTO {
    private Long imageNo;
    private String imageUrl;
    private String imageOrigName;
    private String imageSavedName;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
