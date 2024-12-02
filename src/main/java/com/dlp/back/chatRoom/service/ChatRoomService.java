package com.dlp.back.chatRoom.service;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.character.repository.CharacterRepository;
import com.dlp.back.chatRoom.domain.dto.*;
import com.dlp.back.chatRoom.repository.ChatRoomRepository;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;

import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.member.repository.MemberRepository;
import com.dlp.back.participant.domain.entity.Participant;
import com.dlp.back.participant.repository.ParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ModelMapper modelMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final ParticipantRepository participantRepository;

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

    public List<ChatRoom> checkChatRoom(ChatRoomInfo chatRoomInfo) {
        // charNo와 memberNo로 Participant 조회.
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByCharNoAndMemberNo(
                chatRoomInfo.getCharNo(), chatRoomInfo.getMemberNo());

        return chatRooms;
    }

    public ChatRoom createChatRoom2(ChatRoomInfo chatRoomInfo) {

        Member member = memberRepository.findById(chatRoomInfo.getMemberNo()).get();
        Character character = characterRepository.findById(chatRoomInfo.getCharNo()).get();

        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(chatRoomInfo.getCharName())
                .description(character.getDescription())
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        Participant userParticipant = Participant.builder()
                .chatRoom(savedChatRoom)
                .member(member)
                .build();

        participantRepository.save(userParticipant);

        Participant characterParticipant = Participant.builder()
                .chatRoom(savedChatRoom)
                .character(character)
                .build();

        participantRepository.save(characterParticipant);

        savedChatRoom.setParticipant(new ArrayList<>());
        savedChatRoom.getParticipant().add(userParticipant);
        savedChatRoom.getParticipant().add(characterParticipant);

        return savedChatRoom;
    }

    public ChatRoom createGroupChatRoom(GroupChatRoomInfo chatRoomInfo) {
        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(chatRoomInfo.getGroupName())
                .description(chatRoomInfo.getGroupDescription())
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 멤버 조회
        Member member = memberRepository.findById(chatRoomInfo.getMemberNo()).get();

        Participant userParticipant = Participant.builder()
                .chatRoom(savedChatRoom)
                .member(member)
                .build();

        participantRepository.save(userParticipant);

        // 각 캐릭터에 대해 Participant 등록
        for (Long charNo : chatRoomInfo.getCharNo()) {
            Character character = characterRepository.findById(charNo)
                    .orElseThrow(() -> new RuntimeException("Character not found"));

            Participant characterParticipant = Participant.builder()
                    .chatRoom(savedChatRoom)
                    .character(character)
                    .build();

            participantRepository.save(characterParticipant);
        }

        return savedChatRoom; // 생성된 채팅방 반환
    }

    public ChatRoom findbyId(Long sessionId) {
        return chatRoomRepository.findById(sessionId).get();
    }
}
