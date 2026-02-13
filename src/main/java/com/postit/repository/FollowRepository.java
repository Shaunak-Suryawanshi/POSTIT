package com.postit.repository;

import com.postit.model.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {
    
    List<Follow> findByFollowerId(String followerId);
    
    List<Follow> findByFollowingId(String followingId);
    
    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);
    
    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);
    
    long countByFollowerId(String followerId);  // Following count
    
    long countByFollowingId(String followingId);  // Followers count
    
    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}
