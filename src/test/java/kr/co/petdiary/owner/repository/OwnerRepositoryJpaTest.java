package kr.co.petdiary.owner.repository;

import kr.co.petdiary.global.config.JPATestConfig;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.OwnerCreators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JPATestConfig.class)
class OwnerRepositoryJpaTest {
    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    void OwnerRepository가_NULL이_아님() {
        assertThat(ownerRepository).isNotNull();
    }

    @Test
    void 반려인을_등록() {
        //given
        final Owner saveOwner = OwnerCreators.createOwner();

        //when
        final Owner actual = ownerRepository.save(saveOwner);

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(saveOwner.getEmail());
    }
}
