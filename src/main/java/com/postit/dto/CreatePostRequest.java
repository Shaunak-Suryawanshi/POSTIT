package com.postit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {
    
    @NotBlank(message = "Post content cannot be empty")
    @Size(max = 280, message = "Post content cannot exceed 280 characters")
    private String content;
}
