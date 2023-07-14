package kr.co.petdiary.owner.application;

import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.PasswordInvalidException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerCreatorResponse createOwner(OwnerCreatorRequest request) {
        isEmail(request.getEmail());
        validatePasswordMatch(request.getPassword(), request.getCheckPassword());
        final Owner saveOwner = ownerRepository.save(request.toEntity());
        return OwnerCreatorResponse.toDto(saveOwner);
    }

    private void isEmail(String email) {
        if (ownerRepository.existsByEmail(email)) {
            throw new DuplicatedException(ErrorResult.DUPLICATED_EMAIL);
        }
    }

    private void validatePasswordMatch(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new PasswordInvalidException(ErrorResult.INVALID_PASSWORD);
        }
    }
}
