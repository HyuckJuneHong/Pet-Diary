package kr.co.petdiary.owner.repository;

import kr.co.petdiary.global.config.JPATestConfig;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.model.OwnerCreators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({JPATestConfig.class, OwnerSearchRepository.class})
class OwnerSearchRepositoryJpaTest {
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerSearchRepository ownerSearchRepository;

    @Test
    void OwnerSearchRepository가_NULL이_아님() {
        assertThat(ownerSearchRepository).isNotNull();
    }

    @Test
    void 해당_이메일의_Owner_조회() {
        //given
        final Owner saveOwner = OwnerCreators.createOwner();

        //when
        ownerRepository.save(saveOwner);
        final Owner actual = ownerSearchRepository.searchByEmail(saveOwner.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));

        //then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(saveOwner.getEmail());
    }

    @Test
    void 해당_이메일의_Owner가_없음() {
        //given
        final Owner saveOwner = OwnerCreators.createOwner();

        //when
        final Optional<Owner> actual = ownerSearchRepository.searchByEmail(saveOwner.getEmail());

        //then
        assertThat(actual).isEmpty();
    }
}
