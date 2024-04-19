package com.huy.airbnbserver.user.converter;

import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User source) {
        return new UserDto(
                source.getId(),
                source.getUsername(),
                source.getEmail(),
                source.isEnabled(),
                source.getRoles(),
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }
}
