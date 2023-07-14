package kr.co.petdiary.owner.entity;

import jakarta.validation.ConstraintViolationException;
import kr.co.petdiary.global.config.JpaConfig;
import kr.co.petdiary.owner.model.OwnerCreators;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class OwnerTest {
    @Autowired
    private TestEntityManager testEntityManager;
    
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
