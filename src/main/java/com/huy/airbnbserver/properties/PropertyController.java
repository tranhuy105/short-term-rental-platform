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
import org.springframework.security.core.Authentication;
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
    public Result findById(@NotNull @PathVariable Long propertyId) {
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch successful",
                propertyToPropertyDtoConverter.convert(propertyService.findById(propertyId))
        );
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Result save(@RequestParam("images") List<MultipartFile> images,
                       @RequestPart PropertyDto propertyDto,
                       Authentication authentication) throws IOException {
        if (Utils.imageValidationFailed(images)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        };


        Property property = propertyDtoToPropertyConverter.convert(propertyDto);
        assert property != null;
        var savedProperty = propertyService.save(property, Utils.extractAuthenticationId(authentication), images);

        return new Result(true,
                StatusCode.CREATED,
                "Created Property Success",
                propertyToPropertyDtoConverter.convert(savedProperty));
    }

    @DeleteMapping("/{id}")
    public Result delete(@Valid @PathVariable Long id, Authentication authentication) {
        propertyService.delete(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "delete successful");
    }



    // like service

    @PutMapping("/like/{id}")
    public Result likeProperty(@Valid @PathVariable Long id, Authentication authentication) {
        propertyService.like(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, 200, "Success");
    }

    @DeleteMapping("/like/{id}")
    public Result deleteLike(@Valid @PathVariable Long id, Authentication authentication) {
        propertyService.unlike(id, Utils.extractAuthenticationId(authentication));
        return new Result(true, 200, "Success");
    }

    @GetMapping("/like")
    public Result getLikedPropertiesByUser(Authentication authentication) {
        var propertyList =  propertyService
                .getAllLikedPropertiedByUserWithUserId(Utils.extractAuthenticationId(authentication));

        List<PropertyDto> propertyDtos = propertyList
                .stream()
                .map(propertyToPropertyDtoConverter::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Fetch all liked property", propertyDtos);
    }
}
