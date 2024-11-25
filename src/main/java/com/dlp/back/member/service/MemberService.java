package com.dlp.back.member.service;

import com.dlp.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
//    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;

}
