package kr.co.petdiary.global.auth.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException {
        log.warn("======= OAuth Login Failure ======= ", e.getMessage());

        response.setStatus(ErrorResult.FAILURE_OAUTH_LOGIN.getHttpStatus().value());
        response.getWriter().write(ErrorResult.FAILURE_OAUTH_LOGIN.getMessage());
    }
}
