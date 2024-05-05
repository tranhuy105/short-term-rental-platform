package com.huy.airbnbserver.image;

import com.huy.airbnbserver.system.exception.UnsupportedImageFormatException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ImageUtils {
    public static byte[] compressImage(MultipartFile file, float quality) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new UnsupportedImageFormatException("Only JPG, PNG format are supported");
        }

        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String formatName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        ImageWriter writer = ImageIO.getImageWritersByFormatName(formatName).next();

        JPEGImageWriteParam param = new JPEGImageWriteParam(null);
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        // Write the image to a ByteArrayOutputStream using the specified quality
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
        writer.setOutput(imageOutputStream);
        writer.write(null, new IIOImage(image, null, null), param);

        imageOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }
}
