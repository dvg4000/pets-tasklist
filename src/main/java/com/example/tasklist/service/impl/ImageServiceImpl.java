package com.example.tasklist.service.impl;

import com.example.tasklist.domain.exception.ImageUploadException;
import com.example.tasklist.domain.task.TaskImage;
import com.example.tasklist.service.ImageService;
import com.example.tasklist.service.props.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String upload(final TaskImage image) {
        try {
            createBucketIfNeeded();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }

        final MultipartFile file = image.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name.");
        }

        final String fileName = generateFileName(file);
        try (InputStream inputStream = file.getInputStream()) {
            saveImage(inputStream, fileName);
        } catch (IOException e) {
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }
        return fileName;
    }

    @SneakyThrows
    private void createBucketIfNeeded() {
        final boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateFileName(final MultipartFile file) {
        final String newFileName = UUID.randomUUID().toString();
        return getExtension(file)
                .map(extension -> newFileName + "." + extension)
                .orElse(newFileName);
    }

    private Optional<String> getExtension(final MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .map(name -> Pair.of(name, name.lastIndexOf(".")))
                .filter(pair -> pair.getSecond() > 0)
                .map(pair -> pair.getFirst().substring(pair.getSecond()));
    }

    @SneakyThrows
    private void saveImage(final InputStream inputStream,
                           final String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
    }

}
