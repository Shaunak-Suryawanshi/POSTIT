package com.postit.repository;

import com.postit.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUserId(String userId);
    
    void deleteByUserId(String userId);
    
    void deleteByExpiryDateBefore(LocalDateTime date);
}
