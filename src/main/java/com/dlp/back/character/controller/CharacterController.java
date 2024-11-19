package com.dlp.back.character.controller;

import com.dlp.back.character.domain.dto.CharacterDTO;
import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.character.service.CharacterService;
import com.dlp.back.common.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Character")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
public class CharacterController {
    private final CharacterService characterService;

    @Operation(summary = "전체 캐릭터 조회")
    @GetMapping("")
    public ResponseEntity<ResponseMessage> findAllHospital() {

        List<CharacterDTO> allCharacters = characterService.findAllCharacter();

        log.info("조회된 전체 캐릭터 정보 : {}", allCharacters);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("character", allCharacters);

        log.info(responseMap.toString());

        return ResponseEntity.ok()
                .body
                        (new ResponseMessage(
                                HttpStatus.OK,
                                "전체 캐릭터 목록을 불러옵니다.",
                                responseMap
                        ));
    }

//    @Operation(summary = "캐릭터 아이디로 해당 캐릭터 조회")
    @GetMapping("/{charNo}")
    public ResponseEntity<ResponseMessage> findCharacterById(@PathVariable long charNo) {

        Character foundCharacter = characterService.findCharacterById(charNo);

        log.info("조회된 캐릭터 정보 : {}", foundCharacter);

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("character", foundCharacter);

        return ResponseEntity.ok()
                .body(new ResponseMessage(HttpStatus.OK, charNo+"번 캐릭터의 정보를 불러옵니다...", responseMap));
    }


}