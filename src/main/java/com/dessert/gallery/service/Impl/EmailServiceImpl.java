package com.dessert.gallery.service.Impl;

import com.dessert.gallery.entity.User;
import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.dessert.gallery.repository.UserRepository;
import com.dessert.gallery.service.Interface.EmailService;
import com.dessert.gallery.service.Jwt.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender gmailSender;
    private final JavaMailSender naverSender;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @Value("${email.gmail.id}")
    private String gmailUsername;

    @Value("${email.naver.id}")
    private String naverUsername;

    private final String ePw = createKey();

    @Autowired
    public EmailServiceImpl(@Qualifier("gmail") JavaMailSender gmailSender,
                            @Qualifier("naver") JavaMailSender naverSender,
                            RedisService redisService,
                            UserRepository userRepository,
                            UserServiceImpl userService) {
        this.gmailSender = gmailSender;
        this.naverSender = naverSender;
        this.redisService = redisService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public String sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = gmailSender.createMimeMessage();
        message = commonMessage(recipientEmail, message, "gmail");
        redisService.setEmailOtpDataExpire(ePw, recipientEmail, 60 * 5L);   // 유효 시간 5분
        gmailSender.send(message);

        return ePw;
    }

    @Override
    public String sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = naverSender.createMimeMessage();
        message = commonMessage(recipientEmail, message, "naver");
        redisService.setEmailOtpDataExpire(ePw, recipientEmail, 60 * 5L);   // 유효 시간 5분
        naverSender.send(message);

        return ePw;
    }

    @Override
    public void verifyEmail(String key, HttpServletResponse response) {
        String email = redisService.getEmailOtpData(key);
        if (email == null) {
            throw new UnAuthorizedException("401_NOT_ALLOW", ErrorCode.NOT_ALLOW_WRITE_EXCEPTION);
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setEmailOtp(true);
        user.setDeleted(false);

        redisService.deleteEmailOtpData(key);
        userService.setJwtTokenInHeader(email, response);
    }

    public MimeMessage commonMessage(String recipientEmail, MimeMessage message, String type) throws MessagingException, UnsupportedEncodingException {

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

        if (type == "gmail") {
            message.setFrom(new InternetAddress(gmailUsername,"DG_Admin")); // 보내는 사람의 메일 주소, 보내는 사람 이름
        } else {
            message.setFrom(new InternetAddress(naverUsername,"DG_Admin"));
        }

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

}
