package com.dlp.back.member.controller;

import com.dlp.back.common.ResponseMessage;
import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Member")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    // 유저 정보 수정
    @Operation(summary = "유저 수정")
    @PutMapping("/{memberNo}")
    public ResponseEntity<ResponseMessage> modifyUser(@PathVariable Long memberNo, @RequestBody Map<String, String> requestBody) {

        String nickName = requestBody.get("nickname");
        log.info(nickName);
        Member member = memberService.updateUserInfo(memberNo, nickName);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("member",member);

        return ResponseEntity.created(URI.create("/member/" + memberNo))
                .body(new ResponseMessage(HttpStatus.OK, "유저 수정 성공", responseMap));
    }

    // 유저 삭제
    @Operation(summary = "유저 삭제")
    @DeleteMapping("/{memberNo}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable Long memberNo) {

        memberService.deleteUserById(memberNo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "유저 삭제 성공");

        return ResponseEntity.ok()
                .body(new ResponseMessage(HttpStatus.OK, "유저 삭제 성공", responseMap));

    }

}