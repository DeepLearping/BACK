package com.dlp.back.character.service;

import com.dlp.back.character.domain.dto.CharacterDTO;
import com.dlp.back.character.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import com.dlp.back.character.domain.entity.Character;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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


    public Resource loadCharacterImage(String imageName) throws Exception {
        // 이미지 파일의 경로 설정
        Path basePath = Paths.get("src/main/resources/static/image/characterProfile");
        Path imagePath = basePath.resolve(imageName);
        // 파일 이름은 캐릭터 번호에 따라 다를 수 있음
        Resource image = new UrlResource(imagePath.toUri());

        // 이미지가 존재하는지 확인
        if (image.exists() || image.isReadable()) {
            return image;
        } else {
            throw new Exception("Image not found");
        }
    }

    // chatCount 증가 시키는 메서드(이득규)
    @Transactional
    public boolean incrementChatCount(Long charNo) {
        Optional<Character> characterOpt = characterRepository.findById(charNo);
        if (characterOpt.isPresent()) {
            Character character = characterOpt.get();
            character.setChatCount(character.getChatCount() +1);
            characterRepository.save(character);
            return true;
        }
        return false;
    }

}
