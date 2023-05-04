package com.dessert.gallery.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.entity.File;
import com.dessert.gallery.enums.BoardType;
import com.dessert.gallery.repository.FileRepository;
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
public class S3Service {

    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    public List<FileDto> uploadImages(List<MultipartFile> files) throws IOException {
        return this.existsFiles(files);
    }

    public List<FileDto> updateFiles(Long id, BoardType boardType, List<MultipartFile> files)
                                                                throws IOException {
        // boardType에 따라서 각각의 FK 엔티티와 연결된 File 엔티티 리스트를 가져옴
        List<File> fileList;

        switch (boardType) {
            case NOTICE_BOARD:
                fileList = fileRepository.findByNoticeBoardId(id);
                break;
            case REVIEW_BOARD:
                fileList = fileRepository.findByReviewBoardId(id);
                break;
            case STORE_BOARD:
                fileList = fileRepository.findByStoreBoardId(id);
                break;
            default:
                throw new IllegalArgumentException("Invalid board type: " + boardType);
        }

        // 기존 파일 리스트와 새로 업로드한 파일 리스트를 비교하여
        // 바뀐 파일만 업로드하고, 기존 파일 중 사용하지 않는 파일은 삭제
        List<FileDto> list = this.existsFiles(files);

        for (int i = 0; i < fileList.size(); i++) {
            if (!list.get(i).getFileName().contains(fileList.get(i).getFileName())) {
                s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileList.get(i).getFileName())); // 사용하지 않는 파일 삭제
                fileRepository.delete(fileList.get(i)); // DB에서도 해당 파일 엔티티 삭제
            }
        }

        return list;
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

    private List<FileDto> existsFiles(List<MultipartFile> files) throws IOException {
        List<FileDto> list = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = file.getOriginalFilename();
            String fileName = UUID.randomUUID() + "-" + key;
            if (s3Client.doesObjectExist(bucketName, key)) {
                continue;
            }
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            FileDto requestDto = FileDto.builder()
                    .fileName(fileName)
                    .fileUrl(s3Client.getUrl(bucketName, fileName).toString())
                    .build();
            list.add(requestDto);
        }

        return list;
    }
}
