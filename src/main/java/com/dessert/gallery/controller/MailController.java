package com.dessert.gallery.controller;

import com.dessert.gallery.service.Interface.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/mail")
@Tag(name = "Mail Authorization Controller", description = "메일 인증 API")
public class MailController {

    private final EmailService emailService;

    @Operation(summary = "Gmail 인증 메일 발송 API")
    @PostMapping("/gmail")
    public ResponseEntity<String> sendGmail(String recipientEmail) throws UnsupportedEncodingException, MessagingException {
        emailService.sendGmail(recipientEmail);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @Operation(summary = "Naver 인증 메일 발송 API")
    @PostMapping("/naver")
    public ResponseEntity<String> sendNaver(String recipientEmail) throws UnsupportedEncodingException, MessagingException {
        emailService.sendNaver(recipientEmail);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @Operation(summary = "인증 코드 확인 API")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(String key, HttpServletResponse response) {
        emailService.verifyEmail(key, response);
        return ResponseEntity.ok("2차 인증이 정상적으로 처리되었습니다.");
    }
}
