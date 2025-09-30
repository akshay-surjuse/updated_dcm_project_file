package com.digital.google_meet_config;

import com.digital.entity.Teacher;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.TeacherRepository;
import com.digital.servicei.GoogleRefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class GoogleOAuth2CallbackController {

    private final WebClient webClient;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Value("${google.token.url}")
    private String tokenUrl;

    private final GoogleRefreshTokenService googleRefreshTokenService;
    private final TeacherRepository teacherRepository;

    public GoogleOAuth2CallbackController(WebClient.Builder builder, GoogleRefreshTokenService googleRefreshTokenService, TeacherRepository teacherRepository) {
        this.webClient = builder.build();
        this.googleRefreshTokenService = googleRefreshTokenService;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, @RequestParam("state") Long teacherId) {
        System.out.println("in callback");
        String body = "code=" + code
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&grant_type=authorization_code";

        Map<String, Object> response = webClient.post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        String refreshToken = (String) response.get("refresh_token");

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() ->
                new ResourceNotFoundException("Teacher with id: " + teacherId + " not found in database."));

        GoogleRefreshToken googleRefreshToken = GoogleRefreshToken.builder()
                .refreshToken(refreshToken)
                .teacher(teacher)
                .build();

        googleRefreshTokenService.saveRefreshToken(googleRefreshToken);
        System.out.println("in callback after token saved");
    }
}

