package com.huy.airbnbserver.booking;

import com.huy.airbnbserver.booking.converter.BookingDtoToBookingConverter;
import com.huy.airbnbserver.booking.converter.BookingToBookingDtoConverter;
import com.huy.airbnbserver.booking.dto.BookingDto;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoToBookingConverter bookingDtoToBookingConverter;
    private final BookingToBookingDtoConverter bookingToBookingDtoConverter;

    @PostMapping("/{propertyId}")
    public Result newBooking(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BookingDto bookingDto,
            @Valid @NotNull @PathVariable Long propertyId
            ) {
        var savedBooking = bookingService.save(
                Objects.requireNonNull(bookingDtoToBookingConverter.convert(bookingDto)),
                Integer.valueOf(jwt.getClaimAsString("userId")),
                propertyId
        );
        return new Result(
                true,
                StatusCode.SUCCESS,
                "new booking pending...",
                bookingToBookingDtoConverter.convert(savedBooking)
        );
    }

    @GetMapping
    public Result getAllBookingsByUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        var bookingList = bookingService.getAllBookingByUserId(Integer.valueOf(jwt.getClaimAsString("userId")));
        var bookingDtoList = bookingList
                .stream()
                .map(bookingToBookingDtoConverter::convert)
                .toList();
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch all booking for user with id: " + jwt.getClaimAsString("userId"),
                bookingDtoList
        );
    }

    @GetMapping("/{propertyId}")
    public Result getAllBookingsForPropertyHost(
            @Valid @NotNull
            @PathVariable Long propertyId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        var bookingList = bookingService
                .getAllBookingByPropertyId(propertyId, Integer.valueOf(jwt.getClaimAsString("userId")));
        var bookingDtoList = bookingList
                .stream()
                .map(bookingToBookingDtoConverter::convert)
                .toList();
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch all booking for property with id: " + propertyId,
                bookingDtoList
        );
    }

    @DeleteMapping("/{id}")
    public Result deleteBooking(
            @Valid @NotNull @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        bookingService.delete(id, Integer.valueOf(jwt.getClaimAsString("userId")));
        return new Result(true, StatusCode.SUCCESS, "Delete booking success");
    }

    @PutMapping("/{id}")
    public Result confirmBooking(
            @Valid @NotNull @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        bookingService.confirm(id, Integer.valueOf(jwt.getClaimAsString("userId")));
        return new Result(true, StatusCode.SUCCESS, "Confirm booking success");
    }
}
