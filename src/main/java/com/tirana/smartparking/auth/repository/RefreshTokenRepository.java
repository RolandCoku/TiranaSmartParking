package com.tirana.smartparking.auth.repository;

import com.tirana.smartparking.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenIdHash(String tokenIdHash);
    void deleteByUsername(String username);
}
