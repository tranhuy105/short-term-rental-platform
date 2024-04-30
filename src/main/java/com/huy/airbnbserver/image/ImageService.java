package com.huy.airbnbserver.image;

import com.huy.airbnbserver.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@Transactional
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public List<Image> upload(@NotNull List<MultipartFile> files) throws IOException {

        List<Image> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            var saveImage = Image.builder()
                    .name(file.getOriginalFilename())
                    .imageData(ImageUtils.compressImage(file, 0.2F))
                    .build();
            imageRepository.save(saveImage);
            savedImages.add(saveImage);
        }

        return savedImages;
    }

    public byte[] download(@NotEmpty Long id) {
        Image image = imageRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("image", id)
        );
        return image.getImageData();
    }

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public void deleteById(Long id) {
        imageRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("id", id)
        );
        imageRepository.deleteById(id);
    }
}
