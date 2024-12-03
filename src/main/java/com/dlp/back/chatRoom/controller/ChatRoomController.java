package com.dlp.back.chatRoom.controller;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.chatRoom.domain.dto.*;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.chatRoom.service.ChatRoomService;
import com.dlp.back.common.ResponseMessage;
import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.participant.domain.entity.Participant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "chat_room")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/chatRoom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ModelMapper modelMapper;

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

    //챗팅 방 단일 조회
    @Operation(summary = "챗팅방 조회(1:1)")
    @GetMapping("/select/{sessionId}")
    public ResponseEntity<ResponseMessage> getChatRoom2(@PathVariable Long sessionId){

        // 챗팅방 존재 여부 확인
        ChatRoom chatRoom = chatRoomService.selectChatRoom2(sessionId);

        Map<String, Object> responseMap = new HashMap<>();

        if (chatRoom != null){

            // 1번 인덱스부터 마지막 인덱스까지의 캐릭터를 리스트로 변환
            List<Character> characters = chatRoom.getParticipant().stream()
                    .skip(1) // 1번 인덱스부터 시작
                    .map(Participant::getCharacter) // 각 Participant에서 Character 추출
                    .toList();

            //챗팅방 존재하면 챗팅방 정보를 반환
            ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                    .sessionId(chatRoom.getSessionId())
                    .createdDate(chatRoom.getCreatedDate())
                    .lastModifiedDate(chatRoom.getLastModifiedDate())
                    .roomName(chatRoom.getRoomName())
                    .description(chatRoom.getDescription())
                    .characters(characters) //캐릭터 정보
                    .member(chatRoom.getParticipant().get(0).getMember()) //유저 정보
                    .build();

            responseMap.put("chatRoom", chatRoomResponse);
            return ResponseEntity.ok().body(new ResponseMessage(HttpStatus.OK,"챗팅 방 조회 성공",responseMap));
        } else {
            responseMap.put("message", "챗팅 방이 존재하지 않습니다");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage(HttpStatus.NOT_FOUND,"챗팅 방이 존재하지 않습니다.",responseMap));
        }

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
            chatRoom = chatRooms.get(0);
        } else {
            // 존재하는 채팅방이 없으면 채팅방 생성해주기
            chatRoom = chatRoomService.createChatRoom2(chatRoomInfo);
        }

        // 1번 인덱스부터 마지막 인덱스까지의 캐릭터를 리스트로 변환
        List<Character> characters = chatRoom.getParticipant().stream()
                .skip(1) // 1번 인덱스부터 시작
                .map(Participant::getCharacter) // 각 Participant에서 Character 추출
                .toList();

        ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                .sessionId(chatRoom.getSessionId())
                .createdDate(chatRoom.getCreatedDate())
                .lastModifiedDate(chatRoom.getLastModifiedDate())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .member(chatRoom.getParticipant().get(0).getMember())
                .characters(characters)
                .build();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("chatRoom", chatRoomResponse);

        return ResponseEntity
                .created(URI.create("/chatRoom/"+chatRoom.getSessionId()))
                .body(new ResponseMessage(HttpStatus.CREATED,"챗팅 방 입장!",responseMap));
    }

    //단체 챗팅 방 등록
    @Operation(summary = "단체 챗팅방 등록")
    @PostMapping("/create/groupChat")
    public ResponseEntity<ResponseMessage> createGroupChatRoom(@RequestBody GroupChatRoomInfo chatRoomInfo){

        ChatRoom chatRoom = chatRoomService.createGroupChatRoom(chatRoomInfo);


        // 1번 인덱스부터 마지막 인덱스까지의 캐릭터를 리스트로 변환
        List<Character> characters = chatRoom.getParticipant().stream()
                .skip(1) // 1번 인덱스부터 시작
                .map(Participant::getCharacter) // 각 Participant에서 Character 추출
                .toList();

        ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                .sessionId(chatRoom.getSessionId())
                .createdDate(chatRoom.getCreatedDate())
                .lastModifiedDate(chatRoom.getLastModifiedDate())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .member(chatRoom.getParticipant().get(0).getMember())
                .characters(characters)
                .build();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("chatRoom", chatRoomResponse);

        return ResponseEntity
                .created(URI.create("/chatRoom/"+chatRoom.getSessionId()))
                .body(new ResponseMessage(HttpStatus.CREATED,"챗팅 방 입장!",responseMap));
    }

    // 유저의 따라 챗팅방 전체 조회
    @Operation(summary = "유저별 챗팅방 전체 조회")
    @GetMapping("/{memberNo}")
    public ResponseEntity<ResponseMessage> getAllChatRoomsByMember(@PathVariable Long memberNo) {

        // 챗팅방 존재 유무
        List<ChatRoom> chatRooms = chatRoomService.findChatRoomsByMember(memberNo);

        Map<String, Object> responseMap = new HashMap<>();

        if (!chatRooms.isEmpty()) { // 챗팅방이 존재하는지 확인
            List<ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                    .map(chatRoom -> {
                        // 1번 인덱스부터 마지막 인덱스까지의 캐릭터를 리스트로 변환
                        List<Character> characters = chatRoom.getParticipant().stream()
                                .skip(1) // 1번 인덱스부터 시작
                                .map(Participant::getCharacter) // 각 Participant에서 Character 추출
                                .toList();

                        // 챗팅방 정보를 반환
                        return ChatRoomResponse.builder()
                                .sessionId(chatRoom.getSessionId())
                                .createdDate(chatRoom.getCreatedDate())
                                .lastModifiedDate(chatRoom.getLastModifiedDate())
                                .roomName(chatRoom.getRoomName())
                                .description(chatRoom.getDescription())
                                .characters(characters) // 캐릭터 정보
                                .member(chatRoom.getParticipant().get(0).getMember()) // 유저 정보
                                .build();
                    })
                    .collect(Collectors.toList());

            responseMap.put("chatRooms", chatRoomResponses);
            return ResponseEntity.ok().body(new ResponseMessage(HttpStatus.OK, "챗팅 방 조회 성공", responseMap));
        } else {
            responseMap.put("message", "챗팅 방이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseMessage(HttpStatus.NOT_FOUND, "챗팅 방이 존재하지 않습니다.", responseMap));
        }
    }

}