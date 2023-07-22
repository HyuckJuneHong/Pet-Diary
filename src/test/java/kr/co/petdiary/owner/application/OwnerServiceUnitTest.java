package kr.co.petdiary.owner.application;

import kr.co.petdiary.global.auth.jwt.service.JwtService;
import kr.co.petdiary.global.auth.jwt.service.RedisService;
import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.InvalidPasswordException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.request.OwnerLoginRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.dto.response.OwnerLoginResponse;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.OwnerCreators;
import kr.co.petdiary.owner.model.OwnerDtoCreators;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OwnerServiceUnitTest {
    @InjectMocks
    private OwnerService ownerService;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private OwnerSearchRepository ownerSearchRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtService jwtService;

    @Test
    void 중복된_이메일_검증() {
        //given
        final OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest();
        given(ownerRepository.existsByEmail(any(String.class))).willReturn(true);

        //when, then
        assertThatThrownBy(() -> ownerService.createOwner(request))
                .hasMessage(ErrorResult.DUPLICATED_EMAIL.getMessage())
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void 비밀번호와_확인비밀번호가_일치하지_않음() {
        //given
        final OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest("testCheckPassword");

        //when, then
        assertThatThrownBy(() -> ownerService.createOwner(request))
                .hasMessage(ErrorResult.INVALID_PASSWORD.getMessage())
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 반려인_등록() {
        //given
        final Owner owner = OwnerCreators.createOwner();
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest(owner);
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);
        given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");

        //when
        OwnerCreatorResponse actual = ownerService.createOwner(request);

        //then
        assertThat(actual.name()).isEqualTo(request.getName());
    }

    @Test
    void 로그인_성공() {
        //given
        final Owner owner = OwnerCreators.createOwner();
        final OwnerLoginRequest request = OwnerDtoCreators.ownerLoginRequest(owner.getEmail(), owner.getPassword());
        final String accessToken = "AccessToken";
        final String refreshToken = "RefreshToken";
        given(jwtService.createAccessToken(any(String.class))).willReturn(accessToken);
        given(jwtService.createRefreshToken()).willReturn(refreshToken);
        given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);
        given(ownerSearchRepository.searchByEmail(any(String.class))).willReturn(Optional.of(owner));
        doNothing().when(redisService).setRefreshTokenToRedis(any(String.class), any(String.class));

        //when
        final OwnerLoginResponse actual = ownerService.loginOwner(request);

        //then
        assertThat(actual.accessToken()).isEqualTo(accessToken);
        assertThat(actual.refreshToken()).isEqualTo(refreshToken);
    }
}
