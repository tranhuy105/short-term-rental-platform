package com.huy.airbnbserver.properties.dto;

import com.huy.airbnbserver.image.ImageDto;
import com.huy.airbnbserver.user.dto.UserDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public record PropertyDto(
        Long id,
        @NotNull
        BigDecimal nightly_price,
        @NotEmpty
        String name,
        @NotNull @Min(0)
        Integer max_guests,
        @NotNull @Min(0)
        Integer num_beds,
        @NotNull @Min(0)
        Integer num_bedrooms,
        @NotNull @Min(0)
        Integer num_bathrooms,
        @NotNull @Min(0)
        BigDecimal longitude,
        @NotNull @Min(0)
        BigDecimal latitude,
        @NotEmpty
        String description,
        @NotEmpty
        String address_line,
        Date created_at,
        Date updated_at,
        Integer like_count,
        List<ImageDto> images,
        UserDto host
) {
}
