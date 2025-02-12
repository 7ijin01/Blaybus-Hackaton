package com.blaybus.global.oauth2.handler;

import com.blaybus.global.jwt.JwtUtil;
import com.blaybus.global.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.blaybus.global.oauth2.service.OAuth2UserPrincipal;
import com.blaybus.global.oauth2.user.OAuth2Provider;
import com.blaybus.global.oauth2.user.OAuth2UserUnlinkManager;
import com.blaybus.global.oauth2.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.blaybus.global.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;
import static com.blaybus.global.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("✅ OAuth2 로그인 성공! onAuthenticationSuccess() 실행됨.");

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
        String access = jwtUtil.createAccess(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());
        String refresh = jwtUtil.createRefresh(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());

        if (principal != null) {
            CookieUtils.addCookie(response, "access_token", access, 3600); // 1시간 유효
            CookieUtils.addCookie(response, "refresh_token", refresh, 86400); // 1일 유효
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        log.info("✅ 리디렉션 완료!");
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        log.info("🔍 determineTargetUrl() 실행됨");

        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        log.info("🌐 redirectUri 값: {}", redirectUri);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        log.info("🎯 최종 결정된 targetUrl: {}", targetUrl);

        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        log.info("🔧 mode 값: {}", mode);

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
        if (principal == null) {
            log.warn("⚠️ principal이 null입니다. 로그인 실패로 처리합니다.");
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        if ("login".equalsIgnoreCase(mode)) {
            log.info("🔑 로그인 모드: JWT 토큰 발급");
            String accessToken = "test_access_token";
            String refreshToken = "test_refresh_token";

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("access_token", accessToken)
                    .queryParam("refresh_token", refreshToken)
                    .build().toUriString();
        } else if ("unlink".equalsIgnoreCase(mode)) {
            log.info("🔗 언링크 모드: 사용자 계정 연결 해제");
            String accessToken = principal.getUserInfo().getAccessToken();
            OAuth2Provider provider = principal.getUserInfo().getProvider();

            oAuth2UserUnlinkManager.unlink(provider, accessToken);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .build().toUriString();
        }

        log.error("🚨 mode가 유효하지 않음: {}", mode);
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2UserPrincipal) {
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}