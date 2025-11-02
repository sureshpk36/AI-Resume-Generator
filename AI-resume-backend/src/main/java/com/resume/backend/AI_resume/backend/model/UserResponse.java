package com.resume.backend.AI_resume.backend.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
