package com.huy.airbnbserver.properties;

import com.huy.airbnbserver.image.Image;
import com.huy.airbnbserver.image.ImageDto;
import com.huy.airbnbserver.image.ImageUtils;
import com.huy.airbnbserver.properties.category.Area;
import com.huy.airbnbserver.properties.category.Category;
import com.huy.airbnbserver.properties.category.Tag;
import com.huy.airbnbserver.properties.dto.PropertyOverviewProjection;
import com.huy.airbnbserver.system.exception.EntityAlreadyExistException;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public Property findById(Long id) {
        return propertyRepository.findDetailById(id).orElseThrow(
                () -> new ObjectNotFoundException("property", id)
        );
    }

    public Property save(Property property, Integer userId, List<MultipartFile> images) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        List<Image> savedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            var saveImage = Image.builder()
                    .name(image.getOriginalFilename())
                    .imageData(ImageUtils.compressImage(image.getBytes()))
                    .property(property)
                    .build();
            savedImages.add(saveImage);
        }

        property.setHost(user);
        property.setImages(savedImages);
        return propertyRepository.save(property);
    }

    public void delete(Long id, Integer userId) {
        var deletedProperty = propertyRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("property", id));
        if (!userId.equals(deletedProperty.getHost().getId())) {
            throw new AccessDeniedException("This user dont have access to this resource");
        }

        for (User user : deletedProperty.getLikedByUsers()) {
            user.getLikedProperty().remove(deletedProperty);
        }

        propertyRepository.delete(deletedProperty);
    }


    public void like(Long id, Integer userId) {
        var property = propertyRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("property", id));
        var user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", id));

        if (!propertyRepository.getLikedDetailsOfUserIdAndPropertyId(userId, id).isEmpty()) {
            throw new EntityAlreadyExistException("Liked Entity Associated with this userId and propertyId");
        }

        property.addLikedUser(user);
        propertyRepository.save(property);
    }

    public void unlike(Long id, Integer userId) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("property", id));
        var user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", id));

        if (propertyRepository.getLikedDetailsOfUserIdAndPropertyId(userId, id).isEmpty()) {
            throw new ObjectNotFoundException("liked entity", "userId: " + userId + " propertyId: " + id);
        }

        property.removeLikedUser(user);
        propertyRepository.save(property);
    }

    public List<Property> getAllLikedPropertiedByUserWithUserId(Integer userId) {
        return propertyRepository.getLikedByUserId(userId);
    }

    public List<PropertyOverviewProjection> findAll(
            Category category1,
            Category category2,
            Tag tag,
            Area area,
            Double minNightlyPrice,
            Double maxNightlyPrice,
            Integer minBeds,
            Integer minBathrooms,
            Integer minBedrooms,
            Integer page,
            Integer pageSize
    ) {
        var minLongitude = area != null ? area.getMinLongitude() : null;
        var maxLongitude = area != null ? area.getMaxLongitude() : null;
        var minLatitude = area != null ? area.getMinLatitude() : null;
        var maxLatitude = area != null ? area.getMaxLatitude() : null;

        String _category1 = category1 == null ? null : category1.name();
        String _category2 = category2 == null ? null : category2.name();
        String _tag = tag == null ? null : tag.name();

        int _page = page == null ? 1 : page;
        int _limit = pageSize == null ? 3 : pageSize;
        long offset = ((long) (_page - 1) * _limit);

        List<Object[]> results = propertyRepository.findAllNative(
                _category1,
                _category2,
                _tag,
                area,
                minLongitude,
                maxLongitude,
                minLatitude,
                maxLatitude,
                minNightlyPrice,
                maxNightlyPrice,
                minBeds,
                minBathrooms,
                minBedrooms,
                (long) _limit,
                offset
        );

        return results.stream().map(this::mapToPropertyOverviewProjection).toList();
    }

    private PropertyOverviewProjection mapToPropertyOverviewProjection(Object[] result) {
        return new PropertyOverviewProjection() {
            @Override
            public Long getId() {
                return ((Number) result[0]).longValue();
            }

            @Override
            public BigDecimal getNightlyPrice() {
                return (BigDecimal) result[1];
            }

            @Override
            public String getName() {
                return (String) result[2];
            }

            @Override
            public BigDecimal getLongitude() {
                return ((BigDecimal) result[3]);
            }

            @Override
            public BigDecimal getLatitude() {
                return (BigDecimal) result[4];
            }

            @Override
            public Date getCreatedAt() {
                return (Date) result[5];
            }

            @Override
            public Date getUpdatedAt() {
                return (Date) result[6];
            }

            @Override
            public Integer getNumBeds() {
                return (Integer) result[7];
            }

            @Override
            public List<ImageDto> getImages() {
                String[] imageIds = ((String) result[8]).split(",");
                String[] imageNames = ((String) result[9]).split(",");
                List<ImageDto> images = new ArrayList<>();
                for (int i = 0; i < imageIds.length; i++) {
                    var id = Long.parseLong(imageIds[i]);

                    ImageDto image = new ImageDto(
                            imageNames[i],
                            id,
                            "/api/v1/images/"+id

                    );

                    images.add(image);
                }
                return images;
            }

            @Override
            public BigDecimal getAverageRating() {
                return (BigDecimal) result[10];
            }
        };
    }
}
