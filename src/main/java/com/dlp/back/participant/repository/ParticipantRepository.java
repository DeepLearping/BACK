package com.dlp.back.participant.repository;

import com.dlp.back.participant.domain.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT p FROM Participant p WHERE p.chatRoom.sessionId = :sessionId AND p.character.charNo = :characterId")
    Optional<Participant> findBySessionIdAndCharacterId(Long sessionId, Long characterId);
}
