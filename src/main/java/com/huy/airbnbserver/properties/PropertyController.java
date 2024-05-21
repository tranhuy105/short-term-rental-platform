package com.huy.airbnbserver.properties;

import com.huy.airbnbserver.properties.converter.PropertyOverProjectionToPropertyOverProjectionDto;
import com.huy.airbnbserver.properties.dto.*;
import com.huy.airbnbserver.properties.enm.*;
import com.huy.airbnbserver.properties.converter.PropertyDetailDtoToPropertyConverter;
import com.huy.airbnbserver.properties.converter.PropertyToPropertyDetailDtoConverter;
import com.huy.airbnbserver.properties.converter.PropertyToPropertyOverviewDto;
import com.huy.airbnbserver.system.*;
import com.huy.airbnbserver.system.exception.InvalidSearchQueryException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PropertyController {
    private final PropertyService propertyService;
    private final PropertyToPropertyDetailDtoConverter propertyToPropertyDetailDtoConverter;
    private final PropertyDetailDtoToPropertyConverter propertyDetailDtoToPropertyConverter;
    private final PropertyToPropertyOverviewDto propertyToPropertyOverviewDto;
    private final PropertyOverProjectionToPropertyOverProjectionDto propertyOverProjectionToPropertyOverProjectionDto;

    @GetMapping("/properties/{propertyId}")
    public Result findById(@NotNull @PathVariable Long propertyId) {
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Fetch successful",
                propertyToPropertyDetailDtoConverter.convert(propertyService.findById(propertyId))
        );
    }

    @GetMapping("/properties")
    public Result findMany(
            @RequestParam(value = "tag", required = false) Tag tag,
            @RequestParam(value = "category1", required = false) Category category1,
            @RequestParam(value = "category2", required = false) Category category2,
            @RequestParam(value = "area", required = false) Area area,
            @RequestParam(value = "min_beds", required = false) Integer minBeds,
            @RequestParam(value = "min_bedrooms", required = false) Integer minBedrooms,
            @RequestParam(value = "min_bathrooms", required = false) Integer minBathrooms,
            @RequestParam(value = "min_nightly_price", required = false) Double minNightlyPrice,
            @RequestParam(value = "max_nightly_price", required = false) Double maxNightlyPrice,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "page_size", required = false) Integer pageSize,
            @RequestParam(value = "sort_column", required = false) String sortColumnParam,
            @RequestParam(value = "sort_direction", required = false) String sortDirectionParam
            ) {
        Utils.validateSearchParams(tag, category1, category2, area, minBeds, minBedrooms, minBathrooms, minNightlyPrice, maxNightlyPrice, page, pageSize);

        if (page != null && page < 1) {
            throw new InvalidSearchQueryException("Page must be greater than zero");
        }

        if (pageSize != null && pageSize < 5) {
            throw new InvalidSearchQueryException("Page size must be at least 5");
        }

        if (minBeds != null && minBeds <= 0) {
            throw new InvalidSearchQueryException("instance of min_beds must be greater than zero");
        }

        if (minBedrooms != null && minBedrooms <= 0) {
            throw new InvalidSearchQueryException("instance of min_bedrooms must be greater than zero");
        }

        if (minBathrooms != null && minBathrooms <= 0) {
            throw new InvalidSearchQueryException("instance of min_bathrooms must be greater than zero");
        }


        if ((minNightlyPrice == null && maxNightlyPrice != null) ||
                (minNightlyPrice != null && maxNightlyPrice == null)) {
            throw new InvalidSearchQueryException("Both min price and max price must be provided or both should be null");
        }

        if (minNightlyPrice != null) {
            if (maxNightlyPrice <= minNightlyPrice) {
                throw new InvalidSearchQueryException("Min price can not larger than Max");
            }

            if (minNightlyPrice < 10 || maxNightlyPrice < 50) {
                throw new InvalidSearchQueryException("Min price must be larger than 10, Max Price must be larger than 50");
            }
        }

        SortDirection sortDirection;
        if (sortDirectionParam == null) {
            sortDirection = SortDirection.DESC;
        } else {
            try {
                sortDirection = SortDirection.valueOf(sortDirectionParam.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new InvalidSearchQueryException("Sort direction can only be either 'asc' or 'desc'");
            }
        }

        SortColumn sortColumn;
        if (sortColumnParam == null) {
            sortColumn = SortColumn.updatedAt;
        } else {
            try {
                sortColumn = SortColumn.valueOf(sortColumnParam);
            } catch (IllegalArgumentException ex) {
                throw new InvalidSearchQueryException(
                        "Sort column only accept these value: " + Arrays.toString(SortColumn.values())
                );
            }
        }

        int _page = page == null ? 1 : page;
        int _pageSize = pageSize == null ? 5 : pageSize;

        List<PropertyOverviewProjection> properties = propertyService
                .findAll(category1,
                        category2,
                        tag,
                        area,
                        minNightlyPrice,
                        maxNightlyPrice,
                        minBeds,
                        minBathrooms,
                        minBedrooms,
                        _page,
                        _pageSize,
                        sortColumn,
                        sortDirection);


        Long totalProperty = properties.isEmpty() ? 0 : properties.get(0).getTotalProperty();
        PageMetadata pageData = new PageMetadata((long)_page, (long)_pageSize, totalProperty);

        return new Result(
                        true,
                        200,
                        "Fetch",
                        new PropertyOverviewPageDto(
                                pageData,
                                properties
                                        .stream()
                                        .map(propertyOverProjectionToPropertyOverProjectionDto::convert)
                                        .toList()
                        )
                );
    }

    @PostMapping(path = "/properties",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Result save(@RequestParam(value = "images", required = false) List<MultipartFile> images,
                       @Valid @RequestPart PropertyDetailDto propertyDetailDto,
                       Authentication authentication) throws IOException {
        if (images != null && Utils.imageValidationFailed(images)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        }


        Property property = propertyDetailDtoToPropertyConverter.convert(propertyDetailDto);


        assert property != null;
        var savedProperty = propertyService.save(property, Utils.extractAuthenticationId(authentication), images);

        return new Result(true,
                StatusCode.CREATED,
                "Created Property Success",
                propertyToPropertyDetailDtoConverter.convert(savedProperty));
    }

    @PutMapping("/properties/{propertyId}")
    public Result update(@RequestParam(value = "images", required = false) List<MultipartFile> images,
                         @PathVariable Long propertyId,
                         @Valid @RequestPart PropertyDetailDto propertyDetailDto) throws IOException {
        if (images != null && Utils.imageValidationFailed(images)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        }


        return new Result(
                true, 200, "Update Success",
                propertyToPropertyDetailDtoConverter.convert(
                propertyService.update(propertyId, propertyDetailDtoToPropertyConverter.convert(propertyDetailDto), images))
        );

    }



    // TODO: Authorized user
    @PutMapping("/properties/{propertyId}/images")
    public Result updateImages(@RequestParam(value = "images") List<MultipartFile> images,
                         @PathVariable Long propertyId,
                         Authentication authentication) throws IOException {
        if (images != null && Utils.imageValidationFailed(images)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        }

        return new Result(
                true, 200, "Update Images Success",
                propertyToPropertyDetailDtoConverter.convert(
                        propertyService.updateImages(propertyId, images))
        );
    }

    @DeleteMapping("/properties/{propertyId}")
    public Result delete(@Valid @PathVariable Long propertyId, Authentication authentication) {
        propertyService.delete(propertyId, Utils.extractAuthenticationId(authentication));
        return new Result(true, StatusCode.SUCCESS, "delete successful");
    }



    // like service

    @PutMapping("/users/{userId}/properties/{propertyId}/like")
    public Result likeProperty(@Valid @PathVariable Long propertyId,
                               Authentication authentication,
                               @PathVariable Integer userId) {
        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            throw new AccessDeniedException("Access Denied For This User");
        }

        propertyService.like(propertyId, userId);
        return new Result(true, 200, "Success");
    }

    @DeleteMapping("/users/{userId}/properties/{propertyId}/like")
    public Result deleteLike(@Valid @PathVariable Long propertyId,
                             Authentication authentication,
                             @PathVariable Integer userId) {
        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            throw new AccessDeniedException("Access Denied For This User");
        }

        propertyService.unlike(propertyId, userId);
        return new Result(true, 200, "Success");
    }

    // TODO: Add pagination
    @GetMapping("/users/{userId}/liked-properties")
    public Result getLikedPropertiesByUser(Authentication authentication,
                                           @PathVariable Integer userId,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           @RequestParam(value = "page_size", required = false) Integer pageSize) {
        if (page != null && page < 1) {
            throw new InvalidSearchQueryException("Page must be greater than zero");
        }

        if (pageSize != null && pageSize < 5) {
            throw new InvalidSearchQueryException("Page size must be at least 5");
        }

        if (!userId.equals(Utils.extractAuthenticationId(authentication))) {
            throw new AccessDeniedException("Access Denied For This User");
        }

        int _page = page == null ? 1 : page;
        int _pageSize = pageSize == null ? 5 : pageSize;

        var propertyList =  propertyService
                .getAllLikedPropertiedByUserWithUserId(userId, _page, _pageSize);

        Long totalProperty = propertyList.isEmpty() ? 0 : propertyList.get(0).getTotalProperty();
        PageMetadata pageData = new PageMetadata((long)_page, (long)_pageSize, totalProperty);

        List<PropertyOverviewProjectionDto> propertyDetailDtos = propertyList
                .stream()
                .map(propertyOverProjectionToPropertyOverProjectionDto::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Fetch all liked property", new PropertyOverviewPageDto(
                pageData,
                propertyDetailDtos
        ));
    }
}
