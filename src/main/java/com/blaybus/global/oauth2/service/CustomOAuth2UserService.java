package com.blaybus.global.oauth2.service;

import com.blaybus.domain.register.entity.User;
import com.blaybus.domain.register.repository.UserRepository;
import com.blaybus.global.oauth2.exception.OAuth2AuthenticationProcessingException;
import com.blaybus.global.oauth2.user.GoogleOAuth2UserInfo;
import com.blaybus.global.oauth2.user.OAuth2UserInfo;
import com.blaybus.global.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private OAuth2UserRequest oAuth2UserRequest;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationProcessingException {
        log.info("🔍 OAuth2UserService: 사용자 정보 요청 시작");
        this.oAuth2UserRequest = oAuth2UserRequest;

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        log.info("🔍 OAuth2UserService: 사용자 정보 로드 완료 -> {}", oAuth2User.getAttributes());


        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();
        log.info("registraionId", registrationId);
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("accessToken", accessToken);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId,
                accessToken,
                oAuth2User.getAttributes());

        // OAuth2UserInfo field value validation
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        User existingUser = userRepository.findByGoogleId(oAuth2UserInfo.getId());
        log.info("user", existingUser);

        if (existingUser == null){
            User user = User.builder()
                    .googleId(oAuth2UserInfo.getId())
                    .name(oAuth2UserInfo.getName())
                    .gender("Unknown") // 기본값 설정
                    .build();
            userRepository.save(user);
        }
        return new OAuth2UserPrincipal(oAuth2UserInfo);
    }
}