package com.huy.airbnbserver.user.dto;

import com.huy.airbnbserver.image.Image;
import com.huy.airbnbserver.image.ImageDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record UserDto(
    Integer id,
    @NotEmpty(message = "firstname is required")
    String firstname,
    @NotEmpty(message = "lastname is required")
    String lastname,
    @NotEmpty(message = "email is required")
    @Email(message = "invalid email")
    String email,
    Date createdAt,
    Date updatedAt,
    ImageDto avatar
) {
}
