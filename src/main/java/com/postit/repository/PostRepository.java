package com.postit.repository;

import com.postit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    Page<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Page<Post> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("{ 'userId': { $in: ?0 }, 'isDeleted': false }")
    Page<Post> findByUserIdInAndIsDeletedFalseOrderByCreatedAtDesc(List<String> userIds, Pageable pageable);
    
    long countByUserIdAndIsDeletedFalse(String userId);
}
