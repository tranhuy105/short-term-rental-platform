package com.huy.airbnbserver.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public record BookingDto(
        Long id,

        @Future @NotNull
        Date checkin_date,
        @Future @NotNull
        Date checkout_date,
        @Min(0) @NotNull
        Integer num_alduts,
        @Min(0) @NotNull
        Integer num_childrens,

        @Min(0) @NotNull
        Integer num_pets,

        @Min(0) @NotNull
        BigDecimal nightly_fee,

        @Min(0) @NotNull
        BigDecimal clean_fee,

        @Min(0) @NotNull
        BigDecimal service_fee,

        Date created_at,
        boolean is_confirm
) {
}
