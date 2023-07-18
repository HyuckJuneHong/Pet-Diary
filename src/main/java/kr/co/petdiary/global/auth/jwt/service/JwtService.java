package kr.co.petdiary.global.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String EMAIL_CLAIM = "email";

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

    /**
     * AccessToken 생성
     */
    public String createAccessToken(String email) {
        final Date issueDate = new Date();
        final Date expireDate = new Date(issueDate.getTime() + ACCESS_EXPIRE);

        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withClaim(EMAIL_CLAIM, email)
                .withIssuedAt(issueDate)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * RefreshToken 생성
     * RefreshToken은 Claim 정보 X (재발급 역할만)
     */
    public String createRefreshToken() {
        final Date issueDate = new Date();
        final Date expireDate = new Date(issueDate.getTime() + REFRESH_EXPIRE);

        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withIssuedAt(issueDate)
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    /**
     * AccessToken Header에 등록하기
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_HEADER, accessToken);
        log.info("======= AccessToken resend to Header =======");
    }

    /**
     * AccessToken and ReFreshToken Header에 등록하기
     */
    public void sendTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(ACCESS_HEADER, accessToken);
        response.setHeader(REFRESH_HEADER, refreshToken);
        log.info("======= AccessToken and RefreshToken resend to Header =======");
    }

    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer를 제외하고 순수 토큰만 가져옴
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_HEADER))
                .filter(accessToken -> accessToken.startsWith(BEARER_PREFIX))
                .map(accessToken -> accessToken.replace(BEARER_PREFIX, ""));
    }

    /**
     * 헤더에서 RefreshToken 추출
     * 토큰 형식 : Bearer를 제외하고 순수 토큰만 가져옴
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REFRESH_HEADER))
                .filter(refreshToken -> refreshToken.startsWith(BEARER_PREFIX))
                .map(refreshToken -> refreshToken.replace(BEARER_PREFIX, ""));
    }

    /**
     * AccessToken에서 Email 추출
     * <p>추출 전에 JWT.require()로 검증기 생성</p>
     * <p>verify로 AceessToken 검증 후 유효하다면 getClaim()으로 이메일 추출</p>
     */
    public Optional<String> extractEmailByAccessToken(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            log.warn("======= Not Valid Token =======", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 토큰의 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            log.warn("======= Not Valid Token =======", e.getMessage());
            return false;
        }
    }

    /**
     * RefreshToken DB 저장 (업데이트)
     */
    @Transactional
    public void updateRefreshTokenByEmail(String email, String refreshToken) {
        final Owner owner = ownerSearchRepository.searchByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        owner.updateRefreshToken(refreshToken);
    }

    /**
     * 해당 리프레쉬토큰의 회원 토큰 재발급
     */
    @Transactional
    public void reissueAccessTokenByRefreshToken(HttpServletResponse response, String refreshToken) {
        final Owner owner = ownerSearchRepository.searchByRefreshToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        final String accessToken = createAccessToken(owner.getEmail());
        final String reissueRefreshToken = reissueRefreshToken(owner);
        sendTokens(response, accessToken, reissueRefreshToken);
    }

    /**
     * 리프레쉬 토큰 재발급 및 할당
     */
    private String reissueRefreshToken(Owner owner) {
        final String reIssueRefreshToken = createRefreshToken();
        owner.updateRefreshToken(reIssueRefreshToken);
        ownerRepository.save(owner);
        return reIssueRefreshToken;
    }
}
