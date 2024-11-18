package com.dlp.back.chatMessage.domain.entity;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.participant.domain.entity.Participant;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageNo;

    private String content;

    @CreatedDate
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_no")
    private Participant participantNo;




}

