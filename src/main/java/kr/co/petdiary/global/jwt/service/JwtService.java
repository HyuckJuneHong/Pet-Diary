package kr.co.petdiary.global.jwt.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    @Value("${jwt.access.expire}")
    private long ACCESS_EXPIRE;

    @Value("${jwt.refresh.expire}")
    private long REFRESH_EXPIRE;

    private final OwnerRepository ownerRepository;
    private final OwnerSearchRepository ownerSearchRepository;

    @PostConstruct
    protected void init() {
        SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }

    public String createAccessToken(String email) {
        final Date issueDate = new Date();
        final Date expireDate = new Date(issueDate.getTime() + ACCESS_EXPIRE);

        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setClaims(generateClaims(email))
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        final Date issueDate = new Date();
        final Date expireDate = new Date(issueDate.getTime() + REFRESH_EXPIRE);

        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("======= Invalid JWT token =======", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("======= Expired JWT token =======", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("======= Empty JWT Claims =======", e.getMessage());
        } catch (Exception e) {
            log.warn("======= Not Valid Token =======", e.getMessage());
        }
        return false;
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
                .filter(accessToken -> accessToken.startsWith(BEARER_PREFIX))
                .map(accessToken -> accessToken.replace(BEARER_PREFIX, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REFRESH_HEADER))
                .filter(refreshToken -> refreshToken.startsWith(BEARER_PREFIX))
                .map(refreshToken -> refreshToken.replace(BEARER_PREFIX, ""));
    }

    public Optional<String> extractEmailByAccessToken(String accessToken) {
        try {
            return Optional.ofNullable(Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get(EMAIL_CLAIM, String.class));
        } catch (MalformedJwtException e) {
            log.warn("======= Invalid JWT token =======", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("======= Expired JWT token =======", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("======= Empty JWT Claims =======", e.getMessage());
        } catch (Exception e) {
            log.warn("======= Not Valid Token =======", e.getMessage());
        }
        return Optional.empty();
    }

    @Transactional
    public String reissueAccessTokenByRefreshToken(HttpServletResponse response, String refreshToken) {
        final Owner owner = ownerSearchRepository.searchByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        final String accessToken = createAccessToken(owner.getEmail());
        final String reissueRefreshToken = reissueRefreshToken(owner);
        owner.updateRefreshToken(reissueRefreshToken);
        ownerRepository.save(owner);
        sendTokens(response, accessToken, reissueRefreshToken);
        return accessToken;
    }

    public void sendTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_HEADER, accessToken);
        response.setHeader(REFRESH_HEADER, refreshToken);
        log.info("======= AccessToken and RefreshToken resend to Header =======");
    }

    private String reissueRefreshToken(Owner owner) {
        final String reIssueRefreshToken = createRefreshToken();
        owner.updateRefreshToken(reIssueRefreshToken);
        ownerRepository.save(owner);
        return reIssueRefreshToken;
    }

    private Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private Claims generateClaims(String email) {
        final Claims claims = Jwts.claims();
        claims.put(EMAIL_CLAIM, email);
        return claims;
    }
}
