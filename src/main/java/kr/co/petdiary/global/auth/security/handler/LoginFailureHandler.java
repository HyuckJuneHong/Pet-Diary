package kr.co.petdiary.global.auth.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.model.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "text/plain;charset=UTF-8";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException {
        response.setStatus(ErrorResult.FAILURE_LOGIN.getHttpStatus().value());
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setContentType(CONTENT_TYPE);
        response.getWriter().write(ErrorResult.FAILURE_LOGIN.getMessage());
        log.warn("======= Login Failure ======= ", e.getMessage());
    }
}
