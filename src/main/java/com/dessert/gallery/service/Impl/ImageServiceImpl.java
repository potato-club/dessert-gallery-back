package com.dessert.gallery.service.Impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.dessert.gallery.dto.file.FileRequestDto;
import com.dessert.gallery.entity.*;
import com.dessert.gallery.repository.FileRepository;
import com.dessert.gallery.service.Interface.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final AmazonS3Client s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    public List<File> uploadImages(List<MultipartFile> files, Object entity) throws IOException {
        List<File> list = this.existsFiles(files);
        Class<?> entityType = entity.getClass();

        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            if (entityType.equals(NoticeBoard.class)) {
                file.setNoticeBoard((NoticeBoard) entity);
            } else if (entityType.equals(ReviewBoard.class)) {
                file.setReviewBoard((ReviewBoard) entity);
            } else if (entityType.equals(StoreBoard.class)) {
                file.setStoreBoard((StoreBoard) entity);
            } else if (entityType.equals(Store.class)) {
                file.setStore((Store) entity);
            } else if (entityType.equals(User.class)) {
                file.setUser((User) entity);
            }

            File saveFile = fileRepository.save(file);
            list.set(i, saveFile);
        }
        return list;
    }

    public List<File> updateImages(Object entity, List<MultipartFile> files, List<FileRequestDto> requestDto)
                                                                throws IOException {

        List<File> fileList = new ArrayList<>();

        // entity 값에 따라서 각각의 FK 엔티티와 연결된 File 엔티티 리스트를 가져옴
        if (entity.equals(NoticeBoard.class)) {
            fileList = fileRepository.findByNoticeBoard((NoticeBoard) entity);
        } else if (entity.equals(ReviewBoard.class)) {
            fileList = fileRepository.findByReviewBoard((ReviewBoard) entity);
        } else if (entity.equals(StoreBoard.class)) {
            fileList = fileRepository.findByStoreBoard((StoreBoard) entity);
        } else if (entity.equals(Store.class)) {
            fileList = fileRepository.findByStore((Store) entity);
        } else if (entity.equals(User.class)) {
            fileList = fileRepository.findByUser((User) entity);
        }

        // 기존 파일 리스트와 새로 업로드한 파일 리스트를 비교하여
        // 바뀐 파일만 업로드하고, 기존 파일 중 사용하지 않는 파일은 삭제
        List<File> list = this.existsFiles(files);

        for (int i = 0; i < fileList.size(); i++) {
            if (requestDto.get(i).isDeleted() && fileList.get(i).getFileName().equals(requestDto.get(i).getFileName())) {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileList.get(i).getFileName())); // 사용하지 않는 파일 삭제
                fileRepository.delete(fileList.get(i)); // DB에서도 해당 파일 엔티티 삭제
            }
        }

        for (File file : list) {
            if (entity.equals(NoticeBoard.class)) {
                file.setNoticeBoard((NoticeBoard) entity);
            } else if (entity.equals(ReviewBoard.class)) {
                file.setReviewBoard((ReviewBoard) entity);
            } else if (entity.equals(StoreBoard.class)) {
                file.setStoreBoard((StoreBoard) entity);
            } else if (entity.equals(Store.class)) {
                file.setStore((Store) entity);
            } else if (entity.equals(User.class)) {
                file.setUser((User) entity);
            }

            fileRepository.save(file);
        }

        return list;
    }

    public byte[] downloadImage(String key) throws IOException {
        byte[] content;
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

    private List<File> existsFiles(List<MultipartFile> files) throws IOException {
        List<File> list = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = file.getOriginalFilename();
            if (s3Client.doesObjectExist(bucketName, key)) {
                continue;
            }
            String fileName = UUID.randomUUID() + "-" + key;
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            File image = File.builder()
                    .fileName(fileName)
                    .fileUrl(s3Client.getUrl(bucketName, fileName).toString())
                    .build();
            list.add(image);
        }

        return list;
    }
}
