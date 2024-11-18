package com.dlp.back.participant.service;

import com.dlp.back.participant.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {
//    private final ModelMapper modelMapper;
    private final ParticipantRepository participantRepository;

}
