package com.dlp.back.images.controller;

import com.amazonaws.Response;
import com.dlp.back.images.service.ImagesService;
import org.springframework.core.io.Resource;
import com.dlp.back.images.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOError;
import java.io.IOException;

// Amazon S3 사용
@Tag(name = "Images")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/images")
public class ImagesController {
//    private final S3Service s3Service;
    private final ImagesService imagesService;
//    private final ModelMapper modelMapper;


}
