package kr.co.petdiary.owner.repository;

import jakarta.validation.ConstraintViolationException;
import kr.co.petdiary.global.config.JpaConfig;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.OwnerCreators;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class OwnerRepositoryTest {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void OwnerRepository가_NULL이_아님() {
        assertThat(ownerRepository).isNotNull();
    }

    @Test
    void 반려인을_등록() {
        //given
        final Owner saveOwner = OwnerCreators.createOwnerByCellPhone("010-1111-1111");

        //when
        final Owner actual = ownerRepository.save(saveOwner);

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(saveOwner.getEmail());
    }

    @ParameterizedTest
    @CsvSource(value = {"010-12-5678", "01012345678", "0"})
    void 전화번호_형식이_맞지_않음(String cellPhone) {
        //given
        final Owner actual = OwnerCreators.createOwnerByCellPhone(cellPhone);

        //when, then
        assertThatThrownBy(() -> testEntityManager.persistAndFlush(actual))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"exampenaver.com", "@naver.com", "example@"})
    void 이메일_형식이_맞지_않음(String email) {
        //given
        final Owner actual = OwnerCreators.createOwnerByEmail(email);

        //when, then
        assertThatThrownBy(() -> testEntityManager.persistAndFlush(actual))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
