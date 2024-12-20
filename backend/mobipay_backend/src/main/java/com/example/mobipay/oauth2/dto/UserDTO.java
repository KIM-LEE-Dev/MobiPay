package com.example.mobipay.oauth2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    private Long mobiuserId;

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String name;

    @NotEmpty
    private String picture;

    @NotEmpty
    private String phoneNumber;

    @NotEmpty
    private String role;
}
