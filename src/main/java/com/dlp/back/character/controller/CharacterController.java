package com.dlp.back.character.controller;

import com.dlp.back.character.domain.dto.CharacterDTO;
import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.character.service.CharacterService;
import com.dlp.back.common.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Character")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/character")
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

    @GetMapping("/image/characterProfile/{imageName}")
    public ResponseEntity<Resource> getCharacterImage(@PathVariable String imageName) {
        try {
            Resource image = characterService.loadCharacterImage(imageName); // 서비스에서 이미지 로드

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_PNG) // 이미지 타입
                    .body(image);
        } catch (Exception e) {
            log.error("이미지 로드 오류: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // 캐릭터 선택 시 chatCount 증가(이득규)
    @PutMapping("/{charNo}/incrementChatCount")
    public ResponseEntity<String> incrementChatCount(@PathVariable Long charNo) {
        log.info("Chat count increment request received for charNo: {}", charNo);
        try {
            boolean success = characterService.incrementChatCount(charNo);
            if (success) {
                return ResponseEntity.ok("Chat count incremented successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to increment chat count.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating chat count.");
        }
    }

}