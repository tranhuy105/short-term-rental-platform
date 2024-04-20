package com.huy.airbnbserver.properties.converter;

import com.huy.airbnbserver.image.converter.ImageToImageDtoConverter;
import com.huy.airbnbserver.properties.Property;
import com.huy.airbnbserver.properties.dto.PropertyDto;

import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PropertyToPropertyDtoConverter implements Converter<Property, PropertyDto> {
    private final ImageToImageDtoConverter imageToImageDtoConverter;
    private final UserToUserDtoConverter userToUserDtoConverter;

    @Override
    public PropertyDto convert(Property source) {
        return new PropertyDto(
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
                source.getLikedByUsers().size(),
                source.getImages()
                        .stream()
                        .map(imageToImageDtoConverter::convert)
                        .toList(),
                userToUserDtoConverter.convert(source.getHost())
        );
    }
}
