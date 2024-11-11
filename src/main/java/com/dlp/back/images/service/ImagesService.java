package com.dlp.back.images.service;

import com.dlp.back.images.repository.ImagesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImagesService {
    private final ImagesRepository imagesRepository;
//    private final ModelMapper modelMapper;

}
