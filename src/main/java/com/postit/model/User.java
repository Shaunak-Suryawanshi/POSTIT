package com.postit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;  // Hashed password
    
    private String displayName;
    
    private String bio;
    
    private String profileImageUrl;
    
    // OAuth2 fields
    private String provider;  // "local", "google"
    private String providerId;
    
    @Builder.Default
    private Set<String> roles = new HashSet<>();
    
    @Builder.Default
    private boolean enabled = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
