package com.huy.airbnbserver.comment;

import com.huy.airbnbserver.user.dto.UserDto;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        @NotEmpty
        String content,
        @NotNull @Min(1) @Max(5)
        Integer rating,
        LocalDateTime created_at,
        LocalDateTime updated_at,
        UserDto user
) {
}
