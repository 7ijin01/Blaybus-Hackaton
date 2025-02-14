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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 🔹 프론트엔드 리디렉션 URL 가져오기
        String targetUrl = determineTargetUrl(request, response, authentication);

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);

        if (principal != null) {
            String accessToken = jwtUtil.createAccess(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());
            String refreshToken = jwtUtil.createRefresh(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

            // ✅ 쿠키 설정
            CookieUtils.addCookie(response, "access_token", accessToken, 3600);
            CookieUtils.addCookie(response, "refresh_token", refreshToken, 86400);

            log.info("✅ OAuth 로그인 성공! 쿠키 설정 완료! 리디렉션 실행: {}", targetUrl);
        }

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);

        // ✅ 프론트엔드로 리디렉션 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        String targetUrl = redirectUri.orElse("https://front.blaybus-glowup.com");
        System.out.println(targetUrl);
        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
        if (principal == null) {
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        log.info("🔹 로그인 성공: mode={}, email={}", mode, principal.getUserInfo().getEmail());

        if ("login".equalsIgnoreCase(mode)) {

            String access = jwtUtil.createAccess(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());
            String refresh = jwtUtil.createRefresh(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());

            CookieUtils.addCookie(response, "access_token", access, 3600);
            CookieUtils.addCookie(response, "refresh_token", refresh, 86400);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .build().toUriString();
        }

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