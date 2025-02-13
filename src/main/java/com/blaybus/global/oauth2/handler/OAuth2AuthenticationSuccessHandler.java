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

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
        if (principal == null) {
            response.sendRedirect("https://vercel-test-pi-one-93.vercel.app?error=Login failed");
            return;
        }

        String accessToken = jwtUtil.createAccess(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());
        String refreshToken = jwtUtil.createRefresh(principal.getUserInfo().getEmail(), principal.getUserInfo().getName());

        // ✅ Set-Cookie 적용 (리디렉션 대신 JSON 응답)
        CookieUtils.addCookie(response, "access_token", accessToken, 3600);
        CookieUtils.addCookie(response, "refresh_token", refreshToken, 86400);

        log.info("✅ 쿠키 설정 완료! 클라이언트가 직접 리디렉션 수행 필요");

        // ✅ JSON 응답으로 프론트엔드에 리디렉션 주소 전달
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\": \"success\", \"redirect\": \"https://vercel-test-pi-one-93.vercel.app\"}");
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        System.out.println(redirectUri);
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
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