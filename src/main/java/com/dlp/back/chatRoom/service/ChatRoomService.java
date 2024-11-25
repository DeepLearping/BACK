package com.dlp.back.chatRoom.service;

import com.dlp.back.chatRoom.domain.dto.ChatRoomDTO;
import com.dlp.back.chatRoom.domain.dto.CreateChatRoomDTO;
import com.dlp.back.chatRoom.domain.dto.UpdateChatRoomDTO;
import com.dlp.back.chatRoom.repository.ChatRoomRepository;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ModelMapper modelMapper;
    private final ChatRoomRepository chatRoomRepository;

    // 챗팅 방 전체 조회
    public List<ChatRoomDTO> findAllChatRooms() {

        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();

        return allChatRooms.stream()
                .map(chatRoom -> modelMapper.map(chatRoom,ChatRoomDTO.class))
                .collect(Collectors.toList()); //List 변환
    }

    // 챗팅 방 생성
    public ChatRoom createChatRoom(CreateChatRoomDTO createChatRoomDTO) {

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(createChatRoomDTO.getRoomName())
                .description(createChatRoomDTO.getDescription())
                .createdDate(createChatRoomDTO.getCreateTime())
                .lastModifiedDate(createChatRoomDTO.getLastModifyTime())
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    // 챗팅 방 수정
    public ChatRoom updateChatRoom(Long roomNo, UpdateChatRoomDTO updateChatRoomDTO) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomNo).
                orElseThrow(()-> new EntityNotFoundException(roomNo +"챗팅 방을 찾을 수 없습니다. "));

               chatRoom.setRoomName(updateChatRoomDTO.getRoomName());
               chatRoom.setDescription(updateChatRoomDTO.getDescription());
               chatRoom.setDescription(updateChatRoomDTO.getDescription());

        return chatRoomRepository.save(chatRoom);
    }

    // 챗팅 방 삭제
    public void deleteChatRoomById(Long roomNo) {

        chatRoomRepository.deleteById(roomNo);
    }
}
