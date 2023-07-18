package kr.co.petdiary.global.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.exception.UnsupportedRequestFormatException;
import kr.co.petdiary.global.error.model.ErrorResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CustomJsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_LOGIN_URL = "api/v1/owners/login";
    private static final String CONTENT_TYPE = "application/json";
    private static final String HTTP_METHOD = "POST";
    private static final String USERNAME_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private final ObjectMapper objectMapper;

    public CustomJsonUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_URL, HTTP_METHOD));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new UnsupportedRequestFormatException(ErrorResult.NOT_SUPPORTED_AUTHENTICATION_CONTENT_TYPE);
        }

        //JSON 요청 String으로 변환
        final String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        final Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        final String email = usernamePasswordMap.get(USERNAME_KEY);
        final String password = usernamePasswordMap.get(PASSWORD_KEY);
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        return this.getAuthenticationManager().authenticate(authToken);
    }
}
