package kr.co.petdiary.global.auth.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.auth.jwt.service.JwtService;
import kr.co.petdiary.global.auth.oauth2.userdetail.CustomOAuth2User;
import kr.co.petdiary.global.error.exception.OAuthLoginException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String OAUTH_SIGN_IN_URI = "/api/v1/owners/sign-up/oauth2";

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        log.info("====== Login Success ======");

        try {
            final CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            final String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            final String refreshToken = jwtService.createRefreshToken();
            response.addHeader(ACCESS_HEADER, BEARER_PREFIX + accessToken);
            response.addHeader(REFRESH_HEADER, BEARER_PREFIX + refreshToken);

            //TODO handleLoginIfGuest(response, oAuth2User);
            jwtService.sendTokens(response, accessToken, refreshToken);
            jwtService.updateRefreshTokenByEmail(oAuth2User.getEmail(), refreshToken);
        } catch (Exception e) {
            throw new OAuthLoginException(ErrorResult.FAILURE_OAUTH_LOGIN);
        }
    }

    private void handleLoginIfGuest(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        if (oAuth2User.getRole() == Role.GUEST) {
            //화면단의 추가 정보 입력 폼으로 리다이렉트
            response.sendRedirect(OAUTH_SIGN_IN_URI);

            /** TODO : 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러, 서비스를 만들면 그 시점에 Role Update를 진행
             *  ```
             *      final Owner owner = ownerSearchRepository.searchByEmail(oAuth2User.getEmail())
             *              .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
             *      owner.authorizeOwner(); //@Transaction
             *  ```
             */
        }
    }
}
