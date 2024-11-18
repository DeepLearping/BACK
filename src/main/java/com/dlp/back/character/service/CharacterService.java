package com.dlp.back.character.service;

import com.dlp.back.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterService {
//    private final ModelMapper modelMapper;
    private final CharacterRepository characterRepository;

}
