package com.dessert.gallery.controller;

import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> s3Upload(List<MultipartFile> files) throws IOException {
        s3Service.uploadImages(files);
        return ResponseEntity.ok("사진이 정상적으로 저장되었습니다.");
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> s3Download(@RequestParam String key) {
        try{
            byte[] data = s3Service.downloadImage(key);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=" +
                            URLEncoder.encode(key, "euc-kr"))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }
}
