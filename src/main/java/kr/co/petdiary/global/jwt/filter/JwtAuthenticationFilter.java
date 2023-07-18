package kr.co.petdiary.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.exception.JwtTokenExtractException;
import kr.co.petdiary.global.error.exception.JwtTokenInvalidException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.global.jwt.service.JwtService;
import kr.co.petdiary.global.util.PasswordUtil;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String LOGIN_API = "/api/v1/owners/sign-in";

    private final OwnerSearchRepository ownerSearchRepository;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //일반적인 로그인 요청인지
        if (request.getRequestURI().equals(LOGIN_API)) {
            chain.doFilter(request, response);
            return;
        }

        //AccessToken 만료로 온 요청인지
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::validateToken)
                .orElse(null);

        //AccessToken 만료로 인한 요청인 경우
        if (refreshToken != null) {
            jwtService.reissueAccessTokenByRefreshToken(response, refreshToken);
            return;
        }

        //RefreshToken 유효하지 않은 경우
        processAuthentication(request, response, chain);
    }

    private void processAuthentication(HttpServletRequest request, HttpServletResponse response,
                                       FilterChain chain) throws ServletException, IOException {
        //request에서 액세스 토큰 추출
        final String accessToken = jwtService.extractAccessToken(request)
                .orElseThrow(() -> new JwtTokenExtractException(ErrorResult.FAILURE_JWT_TOKEN_EXTRACTION));
        //유효한 토큰이면, 액세스 토큰에서 Email을 추출
        final String email = jwtService.extractEmailByAccessToken(accessToken)
                .orElseThrow(() -> new JwtTokenInvalidException(ErrorResult.INVALID_JWT_CLAIMS));
        //SecurityContextHolder에 담는 메서드로 전달
        ownerSearchRepository.searchByEmail(email)
                .ifPresentOrElse(this::generateAuthentication,
                        () -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));

        chain.doFilter(request, response);
    }

    private void generateAuthentication(Owner owner) {
        String password = owner.getPassword();

        //소셜 로그인 유저의 비밀번호 임의로 설정
        if (password == null) {
            password = PasswordUtil.generateRandomPassword();
        }

        final UserDetails userDetails = User.builder()
                .username(owner.getEmail())
                .password(password)
                .roles(owner.getRoleKey())
                .build();

        final Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
