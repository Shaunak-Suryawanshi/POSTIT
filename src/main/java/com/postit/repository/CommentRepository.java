package com.postit.repository;

import com.postit.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    Page<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(String postId, Pageable pageable);
    
    long countByPostIdAndIsDeletedFalse(String postId);
    
    Page<Comment> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);
}
