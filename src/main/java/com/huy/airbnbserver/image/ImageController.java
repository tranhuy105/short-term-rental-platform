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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageToImageDtoConverter imageToImageDtoConverter;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadImage(@NotNull @RequestParam("images") List<MultipartFile> files) throws IOException {

        if (Utils.imageValidationFailed(files)) {
            return new Result(false, StatusCode.INVALID_ARGUMENT, "Invalid image files were provided", null);
        }

        var imageDtos = imageService.upload(files).stream()
                .map(imageToImageDtoConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Success", imageDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> downloadImage(@Valid @PathVariable Long id) throws DataFormatException, IOException {
        byte[] imageData = imageService.download(id);
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
