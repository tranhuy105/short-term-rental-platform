package com.huy.airbnbserver.properties.converter;

import com.huy.airbnbserver.properties.Property;
import com.huy.airbnbserver.properties.dto.PropertyDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PropertyDtoToPropertyConverter implements Converter<PropertyDto, Property> {
    @Override
    public Property convert(PropertyDto source) {
        var p = new Property();
        p.setNightlyPrice(source.nightly_price());
        p.setName(source.name());
        p.setMaxGuests(source.max_guests());
        p.setNumBathrooms(source.num_bathrooms());
        p.setNumBedrooms(source.num_bedrooms());
        p.setNumBeds(source.num_beds());
        p.setLongitude(source.longitude());
        p.setLatitude(source.latitude());
        p.setDescription(source.description());
        p.setAddressLine(source.address_line());
        return p;
    }
}
