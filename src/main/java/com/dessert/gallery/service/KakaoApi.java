package com.dessert.gallery.service;

import com.dessert.gallery.error.ErrorCode;
import com.dessert.gallery.error.exception.UnAuthorizedException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KakaoApi {

    @Value("${spring.security.oauth2.client.registration.kakao-domain.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao-domain.redirect-uri}")
    private String kakaoDomainRedirectUri;

    @Value("${spring.security.oauth2.client.registration.kakao-local.redirect-uri}")
    private String kakaoLocalRedirectUri;

    private final RestTemplate restTemplate;
    private static final String reqAccessTokenURL = "https://kauth.kakao.com/oauth/token";
    private static final String reqUserInfoURL = "https://kapi.kakao.com/v2/user/me";

    public String getAccessToken(String authorize_code, HttpServletRequest request) {
        String access_Token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String redirectUri = this.selectRedirectUri(request);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", kakaoClientId);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("code", authorize_code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(reqAccessTokenURL, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(responseEntity.getBody())).getAsJsonObject();
            access_Token = jsonObject.get("access_token").getAsString();
        } else {
            throw new UnAuthorizedException("Failed to get access token!", ErrorCode.KAKAO_ACCESS_TOKEN_FAILED);
        }

        return access_Token;
    }

    public String getUserInfo(String accessToken) {
        String email;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(reqUserInfoURL, HttpMethod.GET, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonObject jsonObject = JsonParser.parseString(Objects.requireNonNull(responseEntity.getBody())).getAsJsonObject();
            JsonObject kakaoAccount = jsonObject.getAsJsonObject("kakao_account");
            email = kakaoAccount.getAsJsonObject().get("email").getAsString();
        } else {
            throw new UnAuthorizedException("Failed to get user info!", ErrorCode.KAKAO_USER_INFO_FAILED);
        }

        return email;
    }

    private String selectRedirectUri(HttpServletRequest request) {
        String originHeader = request.getHeader("Origin");

        if (originHeader.contains("dessert-gallery.com")) {
            return kakaoDomainRedirectUri;
        } else {
            return kakaoLocalRedirectUri;
        }
    }
}
