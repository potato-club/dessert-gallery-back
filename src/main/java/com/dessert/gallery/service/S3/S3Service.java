package com.dessert.gallery.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    public void uploadImages(List<MultipartFile> files) throws IOException {
        for (MultipartFile file : files) {
            String key = file.getOriginalFilename();
            if (s3Client.doesObjectExist(bucketName, key)) {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            }
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));
        }
    }

    public byte[] downloadImage(String key) throws IOException {
        byte[] content = null;
        final S3Object s3Object = s3Client.getObject(bucketName, key);
        final S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            content = IOUtils.toByteArray(stream);
            s3Object.close();
        } catch(final IOException ex) {
            throw new IOException("IO Error Message= " + ex.getMessage());
        }
        return content;
    }
}
