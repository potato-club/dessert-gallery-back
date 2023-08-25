package com.dessert.gallery.config;

import com.dessert.gallery.enums.DeveloperType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Configuration
@RequiredArgsConstructor
public class GithubConfig {

    @Value("${github.secret.front_y}")
    private String frontY;

    @Value("${github.secret.front_d}")
    private String frontD;

    @Value("${github.secret.front_j}")
    private String frontJ;

    @Value("${github.secret.back}")
    private String back;


    public HttpHeaders githubApiHeaders(DeveloperType developerType) {
        HttpHeaders headers = new HttpHeaders();

        switch (developerType) {
            case Y:
                headers.setBearerAuth(frontY);
                break;
            case D:
                headers.setBearerAuth(frontD);
                break;
            case J:
                headers.setBearerAuth(frontJ);
                break;
            case B:
                headers.setBearerAuth(back);
                break;
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Charset", "UTF-8");
        return headers;
    }
}
