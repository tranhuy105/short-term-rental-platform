package com.huy.airbnbserver.booking;

import com.huy.airbnbserver.booking.converter.BookingDtoToBookingConverter;
import com.huy.airbnbserver.booking.converter.BookingToBookingDtoConverter;
import com.huy.airbnbserver.booking.dto.BookingDto;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import com.huy.airbnbserver.system.exception.InvalidDateArgumentException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoToBookingConverter bookingDtoToBookingConverter;
    private final BookingToBookingDtoConverter bookingToBookingDtoConverter;

    @PostMapping("/properties/{propertyId}/bookings")
    public Result newBooking(
            @Valid @RequestBody BookingDto bookingDto,
            @PathVariable Long propertyId,
            Authentication authentication
            ) {
        bookingService.isDateValidCheck(bookingDto, propertyId);

//        var savedBooking = bookingService.save(
//                Objects.requireNonNull(bookingDtoToBookingConverter.convert(bookingDto)),
//                Utils.extractAuthenticationId(authentication),
//                propertyId
//        );
        return new Result(
                true,
                StatusCode.SUCCESS,
                "new booking pending..."
//                bookingToBookingDtoConverter.convert(savedBooking)
        );
    }

    @GetMapping("/users/{userId}/bookings")
    public Result getAllBookingsByUser(Authentication authentication, @PathVariable Integer userId) {
        Integer authId = Utils.extractAuthenticationId(authentication);

        if (!authId.equals(userId)) {
            throw new AccessDeniedException("Access denied for this user");
        }

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

    @GetMapping("/properties/{propertyId}/bookings")
    public Result getAllBookingsForPropertyHost(
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

    @DeleteMapping("/bookings/{id}")
    public Result deleteBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        bookingService.delete(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "Delete booking success");
    }

    @PutMapping("/bookings/{id}")
    public Result confirmBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {
        bookingService.confirm(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "Confirm booking success");
    }
}
