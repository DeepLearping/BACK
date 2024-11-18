package com.dlp.back.character.service;

import com.dlp.back.character.domain.dto.CharacterDTO;
import com.dlp.back.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.dlp.back.character.domain.entity.Character;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterService {
    private final ModelMapper modelMapper;
    private final CharacterRepository characterRepository;

    public List<CharacterDTO> findAllCharacter() {

        List<Character> characters = characterRepository.findAll();

        return characters.stream()
                .map(character -> modelMapper.map(character, CharacterDTO.class))
                .collect(Collectors.toList()); // List로 변환

    }

    public Character findCharacterById(long charNo) {

        Character foundCharacter = characterRepository.findById(charNo).get();

        return foundCharacter;
    }

}
