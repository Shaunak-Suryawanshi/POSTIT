package com.postit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follows")
@CompoundIndex(name = "follower_following_idx", def = "{'followerId': 1, 'followingId': 1}", unique = true)
public class Follow {
    
    @Id
    private String id;
    
    @Indexed
    private String followerId;  // User who is following
    
    @Indexed
    private String followingId;  // User being followed
    
    @CreatedDate
    private LocalDateTime createdAt;
}
