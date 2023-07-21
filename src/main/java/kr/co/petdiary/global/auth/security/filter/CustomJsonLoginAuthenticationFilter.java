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

public class CustomJsonLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_LOGIN_URL = "api/v1/owners/sign-in";
    private static final String CONTENT_TYPE = "application/json";
    private static final String HTTP_METHOD = "POST";
    private static final String USERNAME_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private final ObjectMapper objectMapper;

    public CustomJsonLoginAuthenticationFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(DEFAULT_LOGIN_URL, HTTP_METHOD));
        this.objectMapper = objectMapper;
    }

    /**
     * (1) StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8) <br/>
     * - StreamUtils를 통해 request에서 messageBody(JSON) 반환 <br/>
     * - Ex : { "email" : "name@domain.top" , "password" : "1234" } <br/>
     * <br/> <br/>
     * (2) objectMapper.readValue(messageBody, Map.class) <br/>
     * - messageBody를 objectMapper.readValue()로 Map으로 변환 <br/>
     * - Map의 Key(email, password)로 해당 이메일, 패스워드 추출 <br/>
     * <br/> <br/>
     * (3) new UsernamePasswordAuthenticationToken(email, password) <br/>
     * - UsernamePasswordAuthenticationToken의 principal, credentials로 설정 <br/>
     * - AbstractAuthenticationProcessingFilter.getAuthenticationManager()로 AuthenticationManager 객체를 반환 <br/>
     * - authenticate(Authentication)에 UsernamePasswordAuthenticationToken 객체를 넣고 인증 처리 <br/>
     * - AuthenticationManager 객체 -> ProviderManager -> "SecurityConfig"에서 설정 <br/>
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new UnsupportedRequestFormatException(ErrorResult.NOT_SUPPORTED_AUTHENTICATION_CONTENT_TYPE);
        }

        //(1)
        final String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        //(2)
        final Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
        final String email = usernamePasswordMap.get(USERNAME_KEY);
        final String password = usernamePasswordMap.get(PASSWORD_KEY);

        //(3)
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        return this.getAuthenticationManager().authenticate(authToken);
    }
}
