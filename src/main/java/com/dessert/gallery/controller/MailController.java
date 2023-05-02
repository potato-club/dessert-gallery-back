package com.dessert.gallery.controller;

import com.dessert.gallery.service.Interface.EmailService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/mail")
@Api(tags = {"Mail Authorization Controller"})
public class MailController {

    private final EmailService emailService;

    @Operation(summary = "Gmail 인증 메일 발송 API")
    @PostMapping("/gmail")
    public ResponseEntity<String> sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        emailService.sendGmail(recipientEmail);
        return ResponseEntity.ok("인증 메일이 발송되었습니다.");
    }

    @Operation(summary = "Naver 인증 메일 발송 API")
    @PostMapping("/naver")
    public ResponseEntity<String> sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
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
