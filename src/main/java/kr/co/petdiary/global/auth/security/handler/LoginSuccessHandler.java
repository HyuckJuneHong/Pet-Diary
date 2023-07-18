package kr.co.petdiary.global.auth.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.petdiary.global.auth.jwt.service.JwtService;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.access.expire}")
    private long ACCESS_EXPIRE;

    private final JwtService jwtService;
    private final OwnerRepository ownerRepository;
    private final OwnerSearchRepository ownerSearchRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) {
        final String email = extractUsername(authentication);
        final String accessToken = jwtService.createAccessToken(email);
        final String refreshToken = jwtService.createRefreshToken();
        final Owner owner = ownerSearchRepository.searchByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        owner.updateRefreshToken(refreshToken);
        jwtService.sendTokens(response, accessToken, refreshToken);
        ownerRepository.save(owner);
    }

    private String extractUsername(Authentication authentication) {
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
