package com.dlp.back.character.repository;

import com.dlp.back.character.domain.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {

}
