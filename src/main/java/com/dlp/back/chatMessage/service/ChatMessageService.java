package com.dlp.back.chatMessage.service;

import com.dlp.back.chatMessage.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
//    private final ModelMapper modelMapper;
    private final ChatMessageRepository chatMessageRepository;

}
