package com.dessert.gallery.service.Interface;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public interface EmailService {

    String sendGmail(String recipientEmail) throws MessagingException, UnsupportedEncodingException;

    String sendNaver(String recipientEmail) throws MessagingException, UnsupportedEncodingException;

    void verifyEmail(String key, HttpServletResponse response);
}
