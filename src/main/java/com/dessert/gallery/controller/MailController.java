package com.dessert.gallery.controller;

import com.dessert.gallery.service.Interface.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users/mail")
public class MailController {

    private final EmailService emailService;

    @PostMapping("/gmail")
    public String sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        return emailService.sendGmail(recipientEmail);
    }

    @PostMapping("/naver")
    public String sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        return emailService.sendNaver(recipientEmail);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(String key, HttpServletResponse response) {
        emailService.verifyEmail(key, response);
        return ResponseEntity.ok("2차 인증이 정상적으로 처리되었습니다.");
    }
}
