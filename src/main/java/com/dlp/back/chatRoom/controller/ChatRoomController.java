package com.dlp.back.chatRoom.controller;

import com.dlp.back.chatRoom.domain.dto.ChatRoomDTO;
import com.dlp.back.chatRoom.domain.dto.ChatRoomInfo;
import com.dlp.back.chatRoom.domain.dto.CreateChatRoomDTO;
import com.dlp.back.chatRoom.domain.dto.UpdateChatRoomDTO;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.chatRoom.service.ChatRoomService;
import com.dlp.back.common.ResponseMessage;
import com.dlp.back.member.domain.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "chat_room")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/chatRoom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //챗팅 방 조회
    @Operation(summary = "챗팅방 조회")
    @GetMapping("/select")
    public ResponseEntity<ResponseMessage> findAllChatRooms(){

        List<ChatRoomDTO> chatRooms = chatRoomService.findAllChatRooms();

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("chatRooms", chatRooms);

        return ResponseEntity.ok()
                .body(new ResponseMessage(HttpStatus.OK,"챗팅방 전체 조회 성공",responseMap));
    }

    //챗팅 방 등록
    @Operation(summary = "챗팅방 등록(1:1)")
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createChatRoom(@RequestBody CreateChatRoomDTO createChatRoomDTO){

        ChatRoom chatRoom = chatRoomService.createChatRoom(createChatRoomDTO);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("chatRoom", chatRoom);

        return ResponseEntity
                .created(URI.create("/chatRoom/"+chatRoom.getSessionId()))
                .body(new ResponseMessage(HttpStatus.CREATED,"챗팅 방 등록 성공",responseMap));
    }

    //채팅 방 수정
    @Operation(summary = "챗팅방 수정")
    @PutMapping("/update/{roomNo}")
    public ResponseEntity<ResponseMessage> updateChatRoom(@PathVariable Long roomNo, @RequestBody UpdateChatRoomDTO updateChatRoomDTO){

        ChatRoom chatRoom = chatRoomService.updateChatRoom(roomNo, updateChatRoomDTO);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("chatRoom", chatRoom);

        return ResponseEntity.created(URI.create("/api/v1/chatroom/"+roomNo))
                .body(new ResponseMessage(HttpStatus.OK,"채팅 방 수정 성공",responseMap));
    }

    //채팅 방 삭제
    @Operation(summary = "챗팅방 삭제")
    @DeleteMapping("/delete/{roomNo}")
    public ResponseEntity<ResponseMessage> deleteChatRoom(@PathVariable Long roomNo){

        chatRoomService.deleteChatRoomById(roomNo);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "채팅 방 삭제 성공");

        return ResponseEntity.ok()
                .body(new ResponseMessage(HttpStatus.OK, "채팅 방 삭제 성공", responseMap));

    }

    //챗팅 방 등록(1:1)
    @Operation(summary = "챗팅방 등록(1:1)")
    @PostMapping("/create/oneToOne")
    public ResponseEntity<ResponseMessage> createChatRoom2(@RequestBody ChatRoomInfo chatRoomInfo){

        // 해당 유저와 특정 캐릭터 간의 채팅방이 존재하는지 체크
        List<ChatRoom> chatRooms = chatRoomService.checkChatRoom(chatRoomInfo);

        // ChatRoom이 존재하면 true, 아니면 false를 반환
        boolean isExist = !chatRooms.isEmpty();

        ChatRoom chatRoom;

        if (isExist) {
            chatRoom = chatRooms.iterator().next();
        } else {
            // 존재하는 채팅방이 없으면 채팅방 생성해주기
            chatRoom = chatRoomService.createChatRoom2(chatRoomInfo);
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("chatRoom", chatRoom);

        return ResponseEntity
                .created(URI.create("/chatRoom/"+chatRoom.getSessionId()))
                .body(new ResponseMessage(HttpStatus.CREATED,"챗팅 방 입장!",responseMap));
    }
}