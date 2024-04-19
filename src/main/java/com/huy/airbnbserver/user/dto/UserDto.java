package com.huy.airbnbserver.user.dto;

import com.huy.airbnbserver.image.Image;
import com.huy.airbnbserver.image.ImageDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record UserDto(
    Integer id,
    @NotEmpty(message = "username is required")
    String username,
    @NotEmpty(message = "email is required")
    @Email(message = "invalid email")
    String email,
    Date createdAt,
    Date updatedAt,
    ImageDto avatar
) {
}
