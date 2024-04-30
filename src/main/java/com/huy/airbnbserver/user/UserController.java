package com.huy.airbnbserver.user;


import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import com.huy.airbnbserver.user.converter.UserToUserDtoConverter;
import com.huy.airbnbserver.user.dto.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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

    @Deprecated
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
    public Result findUserById(@PathVariable Integer userId) {
        var user = userService.findById(userId);
        var userDto = userToUserDtoConverter.convert(user);
        return new Result(true, StatusCode.SUCCESS, "Success", userDto);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable Integer userId,
                             Authentication authentication,
                             @RequestBody UserDto userDto) {
        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }


        return new Result(
                true,
                StatusCode.SUCCESS,
                "Updated Success",
                userToUserDtoConverter.convert(userService.update(userId, userDto))
        );
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Integer userId,
                             Authentication authentication) {
        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        userService.delete(userId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

    @PutMapping(path = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result assignAvatar(@PathVariable Integer userId,
                               @NotNull @RequestParam("images") List<MultipartFile> files,
                               Authentication authentication) throws IOException {
        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            return new Result(false,  StatusCode.UNAUTHORIZED, "Action Not Allow For This User");
        }

        userService.assignAvatar(userId, files);
        return new Result(true, 200, "Avatar upload success!");
    }
}
