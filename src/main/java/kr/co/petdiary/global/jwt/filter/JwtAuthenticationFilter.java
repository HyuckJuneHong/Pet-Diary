package kr.co.petdiary.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.global.jwt.service.CustomLoginUserDetailsService;
import kr.co.petdiary.global.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomLoginUserDetailsService loginUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String accessToken;
        final String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::validateToken)
                .orElse(null);

        if (refreshToken != null) {
            accessToken = jwtService.reissueAccessTokenByRefreshToken(response, refreshToken);
        } else {
            accessToken = jwtService.extractAccessToken(request)
                    .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_TOKEN_EXTRACTION));
        }

        generateAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private void generateAuthentication(String accessToken) {
        final String email = jwtService.extractEmailByAccessToken(accessToken)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_EMAIL_EXTRACTION));
        final UserDetails userDetails = loginUserDetailsService.loadUserByUsername(email);
        final Authentication authentication
                = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
