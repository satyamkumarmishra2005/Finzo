package com.Finzo.user_service.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreateRequest {

    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}
