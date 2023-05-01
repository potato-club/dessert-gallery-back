package com.dessert.gallery.controller;

import com.dessert.gallery.service.Interface.EmailService;
import com.dessert.gallery.service.S3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/mail")
public class MailController {

    private final EmailService emailService;
    private final S3Service s3Service;

    @PostMapping("/gmail")
    public ResponseEntity<String> sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        emailService.sendGmail(recipientEmail);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @PostMapping("/naver")
    public ResponseEntity<String> sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        emailService.sendNaver(recipientEmail);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(String key, HttpServletResponse response) {
        emailService.verifyEmail(key, response);
        return ResponseEntity.ok("2차 인증이 정상적으로 처리되었습니다.");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> testS3Upload(List<MultipartFile> files) throws IOException {
        s3Service.uploadImages(files);
        return ResponseEntity.ok("사진이 정상적으로 저장되었습니다.");
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> testS3Download(@RequestParam String key) {
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
