package com.huy.airbnbserver.properties;

import com.huy.airbnbserver.properties.converter.PropertyDtoToPropertyConverter;
import com.huy.airbnbserver.properties.converter.PropertyToPropertyDtoConverter;
import com.huy.airbnbserver.properties.dto.PropertyDto;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@AllArgsConstructor
@RequestMapping("/api/v1/properties")
public class PropertyController {
    private final PropertyService propertyService;
    private final PropertyToPropertyDtoConverter propertyToPropertyDtoConverter;
    private final PropertyDtoToPropertyConverter propertyDtoToPropertyConverter;

    @GetMapping("/{propertyId}")
    public Result findById(@Min(0) @NotNull @PathVariable Long propertyId) {
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch successful",
                propertyToPropertyDtoConverter.convert(propertyService.findById(propertyId))
        );
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Result save(@AuthenticationPrincipal Jwt jwt,
                       @RequestParam("images") List<MultipartFile> images,
                       @RequestPart PropertyDto propertyDto) throws IOException {
        if (Utils.imageValidationFailed(images)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        };

        Integer userId = Integer.valueOf(jwt.getClaimAsString("userId"));
        Property property = propertyDtoToPropertyConverter.convert(propertyDto);
        assert property != null;
        var savedProperty = propertyService.save(property, userId, images);

        return new Result(true,
                StatusCode.CREATED,
                "Created Property Success",
                propertyToPropertyDtoConverter.convert(savedProperty));
    }

    @DeleteMapping("/{id}")
    public Result delete(@Valid @PathVariable Long id,
                         @AuthenticationPrincipal Jwt jwt) {
        propertyService.delete(id, Integer.valueOf(jwt.getClaimAsString("userId")));
        return new Result(true, StatusCode.SUCCESS, "delete successful");
    }



    // like service

    @PutMapping("/like/{id}")
    public Result likeProperty(@Valid @PathVariable Long id,
                               @AuthenticationPrincipal Jwt jwt) {
        propertyService.like(id, Integer.valueOf(jwt.getClaimAsString("userId")));
        return new Result(true, 200, "Success");
    }

    @DeleteMapping("/like/{id}")
    public Result deleteLike(@Valid @PathVariable Long id,
                               @AuthenticationPrincipal Jwt jwt) {
        propertyService.unlike(id, Integer.valueOf(jwt.getClaimAsString("userId")));
        return new Result(true, 200, "Success");
    }

    @GetMapping("/like")
    public Result getLikedPropertiesByUser(@AuthenticationPrincipal Jwt jwt) {
        var propertyList =  propertyService
                .getAllLikedPropertiedByUserWithUserId(Integer.valueOf(jwt.getClaimAsString("userId")));

        List<PropertyDto> propertyDtos = propertyList
                .stream()
                .map(propertyToPropertyDtoConverter::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Fetch all liked property", propertyDtos);
    }
}
