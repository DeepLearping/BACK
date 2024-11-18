package com.dlp.back.participant.controller;

import com.dlp.back.participant.service.ParticipantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "participant")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/participant")
public class ParticipantController {
    private final ParticipantService participantService;
    
}