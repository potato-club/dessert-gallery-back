package com.dessert.gallery.controller;

import com.dessert.gallery.service.Interface.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/mail")
public class MailController {

    private final EmailService emailService;

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
}
