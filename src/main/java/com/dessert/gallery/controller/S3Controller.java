package com.dessert.gallery.controller;

import com.dessert.gallery.dto.file.FileDto;
import com.dessert.gallery.enums.BoardType;
import com.dessert.gallery.service.S3.S3Service;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/s3")
@Api(tags = {"AWS S3 Upload & Download Controller"})
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "시험용 S3 Upload API")
    @PostMapping("/upload")
    public List<FileDto> s3Upload(List<MultipartFile> files) throws IOException {
        return s3Service.uploadImages(files);
    }

    @Operation(summary = "시험용 S3 Update API")
    @PutMapping("")
    public List<FileDto> updateFiles(Long id, BoardType boardType, List<MultipartFile> files) throws IOException {
        return s3Service.updateFiles(id, boardType, files);
    }

    @Operation(summary = "S3 Download API")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> s3Download(@RequestParam String key) {
        try {
            byte[] data = s3Service.downloadImage(key);
            InputStream inputStream = new ByteArrayInputStream(data);
            InputStreamResource resource = new InputStreamResource(inputStream);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=" +
                            URLEncoder.encode(key, "UTF-8"))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }
}
