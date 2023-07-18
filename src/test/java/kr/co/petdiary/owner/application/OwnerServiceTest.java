package kr.co.petdiary.owner.application;

import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.InvalidPasswordException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.OwnerCreators;
import kr.co.petdiary.owner.model.OwnerDtoCreators;
import kr.co.petdiary.owner.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {
    @InjectMocks
    private OwnerService ownerService;

    @Mock
    private OwnerRepository ownerRepository;

    @Test
    void 반려인_등록() {
        //given
        Owner owner = OwnerCreators.createOwner();
        given(ownerRepository.save(any(Owner.class))).willReturn(owner);
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest(owner);

        //when
        OwnerCreatorResponse actual = ownerService.createOwner(request);

        //then
        assertThat(actual.name()).isEqualTo(request.getName());
    }

    @Test
    void 중복된_이메일_검증() {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest();
        given(ownerRepository.existsByEmail(any(String.class))).willReturn(true);

        //when, then
        assertThatThrownBy(() -> ownerService.createOwner(request))
                .hasMessage(ErrorResult.DUPLICATED_EMAIL.getMessage())
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void 비밀번호와_확인비밀번호가_일치하지_않음() {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest("testCheckPassword");

        //when, then
        assertThatThrownBy(() -> ownerService.createOwner(request))
                .hasMessage(ErrorResult.INVALID_PASSWORD.getMessage())
                .isInstanceOf(InvalidPasswordException.class);
    }
}
