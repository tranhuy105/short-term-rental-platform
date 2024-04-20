package com.huy.airbnbserver.user;


import com.huy.airbnbserver.system.Utils;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.user.converter.UserDtoToUserConverter;
import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import com.huy.airbnbserver.user.dto.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserToUserDtoConverter userToUserDtoConverter;
    private final UserDtoToUserConverter userDtoToUserConverter;

    @GetMapping
    public Result findAllUsers() {
        List<User> users = userService.findAll();

        List<UserDto> userDtos = users
                .stream()
                .map(userToUserDtoConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Fetch All User", userDtos);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable Integer userId,
                               @AuthenticationPrincipal Jwt jwt) {
        if (Utils.userIdNotMatch(jwt, userId)) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        var user = userService.findById(userId);
        var userDto = userToUserDtoConverter.convert(user);
        return new Result(true, StatusCode.SUCCESS, "Success", userDto);
    }

    @PostMapping
    public Result saveNewUser(@Valid @RequestBody User user) {
        var savedUser = userService.save(user);
        var savedUserDto = userToUserDtoConverter.convert(savedUser);
        return new Result(true, StatusCode.CREATED, "New User Created", savedUserDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId,
                             @Valid @RequestBody UserDto userDto,
                             @AuthenticationPrincipal Jwt jwt
    ) {
        if (Utils.userIdNotMatch(jwt, userId)) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        User update = this.userDtoToUserConverter.convert(userDto);
        assert update != null;
        User updatedUser = this.userService.update(userId, update);
        UserDto updatedUserDto = this.userToUserDtoConverter.convert(updatedUser);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId,
                             @AuthenticationPrincipal Jwt jwt) {
        if (Utils.userIdNotMatch(jwt, userId)) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        this.userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

    @PutMapping(path = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result assignAvatar(@PathVariable Integer userId,
                               @NotNull @RequestParam("images") List<MultipartFile> files,
                               @AuthenticationPrincipal Jwt jwt) throws IOException {
        if (Utils.userIdNotMatch(jwt, userId)) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        userService.assignAvatar(userId, files);
        return new Result(true, 200, "Avatar upload success!");
    }
}
