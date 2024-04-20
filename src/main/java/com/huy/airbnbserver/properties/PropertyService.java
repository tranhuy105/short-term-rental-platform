package com.huy.airbnbserver.properties;

import com.huy.airbnbserver.image.Image;
import com.huy.airbnbserver.image.ImageUtils;
import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import com.huy.airbnbserver.user.User;
import com.huy.airbnbserver.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow(
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

        property.addLikedUser(user);
        propertyRepository.save(property);
    }

    public void unlike(Long id, Integer userId) {
        Property property = propertyRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("property", id));
        var user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", id));

        property.removeLikedUser(user);
        propertyRepository.save(property);
    }

    public List<Property> getAllLikedPropertiedByUserWithUserId(Integer userId) {
        return propertyRepository.getLikedByUserId(userId);
    }
}
