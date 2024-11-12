package com.dlp.back.member.repository;

import com.dlp.back.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByGoogleId(String googleId);

    Optional<Member> findByEmail(String email);
}
