package com.huy.airbnbserver.image;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {
    public static byte[] compressImage(MultipartFile file, float quality) throws IOException {
        // Convert MultipartFile to BufferedImage
        InputStream fileInputStream = new ByteArrayInputStream(file.getBytes());
        BufferedImage image = ImageIO.read(fileInputStream);

        // Get a ImageWriter for jpeg format
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        // Instantiate an ImageWriteParam object with default compression options
        ImageWriteParam param = writer.getDefaultWriteParam();

        // Set the compression quality
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality); // The quality parameter

        // Write the image to a ByteArrayOutputStream using the specified quality
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
        writer.setOutput(imageOutputStream);
        writer.write(null, new IIOImage(image, null, null), param);

        return byteArrayOutputStream.toByteArray();
    }
}
