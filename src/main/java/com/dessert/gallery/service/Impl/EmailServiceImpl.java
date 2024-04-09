package com.dessert.gallery.service.Impl;

import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.NotFoundException;
import com.dessert.gallery.repository.User.UserRepository;
import com.dessert.gallery.service.Interface.EmailService;
import com.dessert.gallery.service.Jwt.RedisJwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender gmailSender;
    private final JavaMailSender naverSender;
    private final RedisJwtService redisJwtService;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @Value("${email.gmail.id}")
    private String gmailUsername;

    @Value("${email.naver.id}")
    private String naverUsername;

    @Autowired
    public EmailServiceImpl(@Qualifier("gmail") JavaMailSender gmailSender,
                            @Qualifier("naver") JavaMailSender naverSender,
                            RedisJwtService redisJwtService,
                            UserRepository userRepository,
                            UserServiceImpl userService) {
        this.gmailSender = gmailSender;
        this.naverSender = naverSender;
        this.redisJwtService = redisJwtService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        redisJwtService.deleteExistingOtp(recipientEmail); // 먼저 요청한 otp code 가 있었다면 제거함.
        MimeMessage message = gmailSender.createMimeMessage();

        String ePw = createKey();
        commonMessage(recipientEmail, message, "gmail", ePw);

        redisJwtService.setEmailOtpDataExpire(ePw, recipientEmail, 60 * 5L);   // 유효 시간 5분
        gmailSender.send(message);
    }

    @Override
    public void sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        redisJwtService.deleteExistingOtp(recipientEmail); // 먼저 요청한 otp code 가 있었다면 제거함.
        MimeMessage message = naverSender.createMimeMessage();

        String ePw = createKey();
        commonMessage(recipientEmail, message, "naver", ePw);

        redisJwtService.setEmailOtpDataExpire(ePw, recipientEmail, 60 * 5L);   // 유효 시간 5분
        naverSender.send(message);
    }

    @Override
    public void verifyEmail(String key, HttpServletResponse response) {
        String email = redisJwtService.getEmailOtpData(key).toString();

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NotFoundException("Email Not Found", ErrorCode.NOT_FOUND_EXCEPTION);
        });

        user.setEmailOtp(true);
        user.setDeleted(false);

        redisJwtService.deleteEmailOtpData(key);
        userService.setJwtTokenInHeader(email, response);
    }

    private void commonMessage(String recipientEmail, MimeMessage message, String type, String ePw) throws MessagingException, UnsupportedEncodingException {

        message.addRecipients(MimeMessage.RecipientType.TO, recipientEmail); // to 보내는 대상
        message.setSubject("디저트 갤러리 인증 코드: "); //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        String msg="";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw;
        msg += "</td></tr></tbody></table></div>";

        message.setText(msg, "utf-8", "html"); // 내용, charset 타입, subType

        if (type.equals("gmail")) {
            message.setFrom(new InternetAddress(gmailUsername,"DG_Admin")); // 보내는 사람의 메일 주소, 보내는 사람 이름
        } else {
            message.setFrom(new InternetAddress(naverUsername,"DG_Admin"));
        }
    }

    public static String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }

        return key.toString();
    }

}
