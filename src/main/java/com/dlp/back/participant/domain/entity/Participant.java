package com.dlp.back.participant.domain.entity;

import com.dlp.back.character.domain.entity.Character;
import com.dlp.back.chatRoom.domain.entity.ChatRoom;
import com.dlp.back.member.domain.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "participant")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_no")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_no")
    private Character character;

}

