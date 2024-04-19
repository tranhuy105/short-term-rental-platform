package com.huy.airbnbserver.image;

import com.huy.airbnbserver.image.converter.ImageToImageDtoConverter;
import com.huy.airbnbserver.system.Result;
import com.huy.airbnbserver.system.StatusCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageToImageDtoConverter imageToImageDtoConverter;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadImage(@NotNull @RequestParam("images") List<MultipartFile> files) throws IOException {
        boolean validFilePresent = files.stream()
                .allMatch(file -> {
                    String contentType = file.getContentType();
                    return contentType != null && contentType.startsWith("image/");
                });

        if (!validFilePresent) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        }

        var imageDtos = imageService.upload(files).stream()
                .map(imageToImageDtoConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Success", imageDtos);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> downloadImage(@PathVariable String name) throws DataFormatException, IOException {
        byte[] imageData = imageService.download(name);
        return ResponseEntity.status(200).contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(imageData);
    }

    @GetMapping
    public Result getAll() {
        var imageDtos = imageService.findAll().stream()
                .map(imageToImageDtoConverter::convert)
                .toList();
        return new Result(true, 200, "fetch all image", imageDtos);
    }
}
