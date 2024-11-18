package com.dlp.back.chatRoom.service;

import com.dlp.back.chatRoom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
//    private final ModelMapper modelMapper;
    private final ChatRoomRepository chatRoomRepository;

}
