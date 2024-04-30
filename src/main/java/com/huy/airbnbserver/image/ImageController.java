package com.huy.airbnbserver.image;

import com.huy.airbnbserver.image.converter.ImageToImageDtoConverter;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import com.huy.airbnbserver.system.Utils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageToImageDtoConverter imageToImageDtoConverter;

    @GetMapping("/{id}")
    public ResponseEntity<?> downloadImage(@Valid @PathVariable Long id) throws DataFormatException, IOException {
        byte[] imageData = imageService.download(id);
        return ResponseEntity.status(200).contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(imageData);
    }

    @Deprecated
    @GetMapping
    public Result getAll() {
//        var imageDtos = imageService.findAll().stream()
//                .map(imageToImageDtoConverter::convert)
//                .toList();
//        return new Result(true, 200, "fetch all image", imageDtos);
        throw new AccessDeniedException("Deprecated Routes, About To Remove Soon");
    }
}
