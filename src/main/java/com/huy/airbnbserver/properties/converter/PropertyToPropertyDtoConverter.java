package com.huy.airbnbserver.properties.converter;

import com.huy.airbnbserver.booking.BookingRepository;
import com.huy.airbnbserver.image.converter.ImageToImageDtoConverter;
import com.huy.airbnbserver.properties.Property;
import com.huy.airbnbserver.properties.PropertyRepository;
import com.huy.airbnbserver.properties.category.Category;
import com.huy.airbnbserver.properties.dto.PropertyDetailDto;

import com.huy.airbnbserver.properties.dto.ReviewInfoProjection;
import com.huy.airbnbserver.system.Utils;
import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PropertyToPropertyDtoConverter implements Converter<Property, PropertyDetailDto> {
    private final ImageToImageDtoConverter imageToImageDtoConverter;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;

    @Override
    public PropertyDetailDto convert(Property source) {
        Double averageRating = (double) 0;
        Integer totalRating = 0;

        ReviewInfoProjection averageAndTotalRating = propertyRepository.findAverageRatingForProperty(source.getId());

        if (averageAndTotalRating != null) {
            averageRating = averageAndTotalRating.getAverageRating();
            totalRating = averageAndTotalRating.getTotalRating();
        }

        return new PropertyDetailDto(
                source.getId(),
                source.getNightlyPrice(),
                source.getName(),
                source.getMaxGuests(),
                source.getNumBeds(),
                source.getNumBedrooms(),
                source.getNumBathrooms(),
                source.getLongitude(),
                source.getLatitude(),
                source.getDescription(),
                source.getAddressLine(),
                source.getCreatedAt(),
                source.getUpdatedAt(),
                source.getImages()
                        .stream()
                        .map(imageToImageDtoConverter::convert)
                        .toList(),
                userToUserDtoConverter.convert(source.getHost()),
                averageRating,
                totalRating,
                Utils.fillDateRanges(bookingRepository.findAllBookingDateOfProperty(source.getId())),
                source.getCategories().stream().map(Enum::name).collect(Collectors.toSet()),
                source.getTag().name()
        );
    }
}
