package kr.co.petdiary.global.auth.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.auth.context.OwnerThreadLocal;
import kr.co.petdiary.global.auth.jwt.service.JwtService;
import kr.co.petdiary.global.auth.jwt.service.RedisService;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.exception.ExpiredJwtTokenException;
import kr.co.petdiary.global.error.exception.InvalidJwtTokenException;
import kr.co.petdiary.global.error.exception.MalformedJwtTokenException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final RedisService redisService;
    private final OwnerSearchRepository ownerSearchRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = jwtService.extractAccessToken(request)
                    .filter(jwtService::validateToken)
                    .orElse(null);

            if (accessToken == null) {
                final String refreshToken = jwtService.extractRefreshToken(request)
                        .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_TOKEN_EXTRACTION));

                accessToken = redisService.reissueAccessTokenByRefreshToken(response, refreshToken);
            }

            generateAuthentication(accessToken);
        } catch (ExpiredJwtTokenException | InvalidJwtTokenException | MalformedJwtTokenException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
        }

        filterChain.doFilter(request, response);
    }

    private void generateAuthentication(String accessToken) {
        final String email = jwtService.extractEmailByAccessToken(accessToken)
                .orElseThrow(() -> new InvalidJwtTokenException(ErrorResult.FAILURE_JWT_EMAIL_EXTRACTION));
        final Owner owner = ownerSearchRepository.searchByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        final UserDetails userDetails = User.builder()
                .username(owner.getEmail())
                .password(owner.getPassword())
                .roles(owner.getRole().name())
                .build();
        final Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        OwnerThreadLocal.setOwner(owner);
        log.info("======= generateAuthentication(...) - Owner ThreadLocal Set");
    }
}
