package com.postit.repository;

import com.postit.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
