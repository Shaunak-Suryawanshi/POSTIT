package com.postit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
@CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}")
public class Post {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 280, message = "Post content cannot exceed 280 characters")
    private String content;
    
    @Builder.Default
    private boolean isDeleted = false;
    
    @Builder.Default
    private int likeCount = 0;
    
    @Builder.Default
    private int commentCount = 0;
    
    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
