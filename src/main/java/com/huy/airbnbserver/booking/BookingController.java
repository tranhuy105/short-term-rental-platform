package com.huy.airbnbserver.booking;

import com.huy.airbnbserver.booking.converter.BookingDtoToBookingConverter;
import com.huy.airbnbserver.booking.converter.BookingToBookingDtoConverter;
import com.huy.airbnbserver.booking.dto.BookingDto;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
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
            @Valid @RequestBody BookingDto bookingDto,
            @Valid @NotNull @PathVariable Long propertyId,
            Authentication authentication
            ) {
        var savedBooking = bookingService.save(
                Objects.requireNonNull(bookingDtoToBookingConverter.convert(bookingDto)),
                Utils.extractAuthenticationId(authentication),
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
    public Result getAllBookingsByUser(Authentication authentication) {
        Integer userId = Utils.extractAuthenticationId(authentication);
        var bookingList = bookingService.getAllBookingByUserId(userId);
        var bookingDtoList = bookingList
                .stream()
                .map(bookingToBookingDtoConverter::convert)
                .toList();
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch all booking for user with id: " + userId,
                bookingDtoList
        );
    }

    @GetMapping("/{propertyId}")
    public Result getAllBookingsForPropertyHost(
            @Valid @NotNull
            @PathVariable Long propertyId,
            Authentication authentication
    ) {
        var bookingList = bookingService
                .getAllBookingByPropertyId(propertyId, Utils.extractAuthenticationId(authentication));
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
            Authentication authentication
    ) {
        bookingService.delete(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "Delete booking success");
    }

    @PutMapping("/{id}")
    public Result confirmBooking(
            @Valid @NotNull @PathVariable Long id,
            Authentication authentication
    ) {
        bookingService.confirm(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "Confirm booking success");
    }
}
