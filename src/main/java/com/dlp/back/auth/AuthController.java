package com.dlp.back.auth;


import com.dlp.back.auth.handler.JwtTokenProvider;
import com.dlp.back.common.ResponseMessage;
import com.dlp.back.member.domain.entity.Member;
import com.dlp.back.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;
    @Value("${url.google.access-token}")
    private String accessTokenUrl;
    @Value("${url.google.profile}")
    private String profileUrl;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${url.kakao.access-token}")
    private String kakaoAccessTokenUrl;


    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        // 1. 구글에 access token 요청
        String tokenUrl = accessTokenUrl;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format("code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
                code, clientId, clientSecret, redirectUri);

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        // 2. 액세스 토큰 반환
        String accessToken = extractAccessToken(response.getBody());

        // 3. 사용자 정보 요청
        String userInfoUrl = profileUrl + accessToken;
        ResponseEntity<String> userInfoResponse = restTemplate.getForEntity(userInfoUrl, String.class);

        // 4. 사용자 정보 처리 및 회원가입 로직
        String userInfo = userInfoResponse.getBody();
        Member member = processUserInfo(userInfo);

        // 5. 백엔드 서버 access token 생성하여 프론트 서버로 전달
        String backendAccessToken = jwtTokenProvider.generateToken(member); // 사용자 정보를 기반으로 JWT 생성

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", backendAccessToken);
        responseMap.put("user", member);

        log.info("backendAccessToken : {}", backendAccessToken);

        return ResponseEntity
                .ok()
                .body(new ResponseMessage(HttpStatus.CREATED, "로그인 성공", responseMap)); // 백엔드 액세스 토큰 반환
    }

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {

        log.info("잘 들어왔니?");
        // 1. 카카오에 access token 요청
        String tokenUrl = kakaoAccessTokenUrl;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        log.info(String.valueOf(requestEntity));
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        // 2. 액세스 토큰 반환
        String accessToken = extractAccessToken(response.getBody());
        log.info("accessToken : {}", accessToken);


        // 3. 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers2.setBearerAuth(accessToken);
        HttpEntity<String> requestEntity2 = new HttpEntity<>(headers2);
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity2, String.class);

        // 4. 사용자 정보 처리 및 회원가입 로직
        String userInfo = userInfoResponse.getBody();
        log.info("userInfo: {}", userInfo);

        Member member = processKakaoUserInfo(userInfo);

        // 5. 백엔드 서버 access token 생성하여 프론트 서버로 전달
        String backendAccessToken = jwtTokenProvider.generateToken(member); // 사용자 정보를 기반으로 JWT 생성

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", backendAccessToken);
        responseMap.put("user", member);

        log.info("backendAccessToken : {}", backendAccessToken);

        return ResponseEntity
                .ok()
                .body(new ResponseMessage(HttpStatus.CREATED, "로그인 성공", responseMap)); // 백엔드 액세스 토큰 반환
    }

    private String extractAccessToken(String responseBody) {
        // JSON 파싱을 통해 access token 추출
        try {
            // Jackson ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // access_token을 추출
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환
        }
    }

    private Member processUserInfo(String userInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(userInfo);

            String googleId = jsonNode.get("sub").asText(); // 구글 ID
            String name = jsonNode.get("name").asText(); // 사용자 이름
            String email = jsonNode.get("email").asText(); // 이메일
            String picture = jsonNode.get("picture").asText(); // 프로필 사진

            Member user = memberRepository.findByGoogleId(googleId);
            if (user == null) {
                // 사용자 정보가 없으면 새로운 사용자 생성
                user = new Member();
                user.setGoogleId(googleId);
                user.setName(name);
                user.setEmail(email);
                user.setPicture(picture);
                user.setRole("human");
                memberRepository.save(user); // 데이터베이스에 저장
            }
            log.info("user 정보 : {}", user);
            return user; // 사용자 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환
        }
    }

    private Member processKakaoUserInfo(String userInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(userInfo);

            String kakaoId = jsonNode.get("id").asText(); //  ID
            log.info("kakaoId : {}", kakaoId);

            // kakao_account에서 사용자 이름과 이메일 정보 가져오기
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            String name = kakaoAccount.get("name").asText(); // 사용자 이름
            log.info("name : {}", name);
            String nickName = kakaoAccount.get("profile").get("nickname").asText(); // 사용자 닉네임
            log.info("nickName : {}", nickName);
            String email = kakaoAccount.get("email").asText(); // 이메일
            log.info("email : {}", email);
            String picture = kakaoAccount.get("profile").get("profile_image_url").asText(); // 프로필 사진
            log.info("picture : {}", picture);

            Member user = memberRepository.findByKakaoId(kakaoId);
            if (user == null) {
                // 사용자 정보가 없으면 새로운 사용자 생성
                user = new Member();
                user.setKakaoId(kakaoId);
                user.setNickname(nickName);
                user.setName(name);
                user.setEmail(email);
                user.setPicture(picture);
                user.setRole("human");
                memberRepository.save(user); // 데이터베이스에 저장
            }
            log.info("user 정보 : {}", user);
            return user; // 사용자 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 오류 발생 시 null 반환
        }
    }

}
