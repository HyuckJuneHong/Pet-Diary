package kr.co.petdiary.owner.application;

import kr.co.petdiary.global.error.exception.DuplicatedException;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.exception.InvalidPasswordException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.global.jwt.service.JwtService;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.request.OwnerLoginRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.dto.response.OwnerLoginResponse;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerRepository;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {
    private final OwnerRepository ownerRepository;
    private final OwnerSearchRepository ownerSearchRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public OwnerCreatorResponse createOwner(OwnerCreatorRequest request) {
        isEmail(request.getEmail());
        validatePasswordMatch(request.getPassword(), request.getCheckPassword());
        final Owner saveOwner = request.toOwner();
        saveOwner.encodePassword(passwordEncoder);
        return OwnerCreatorResponse.toDto(ownerRepository.save(saveOwner));
    }

    @Transactional
    public OwnerLoginResponse loginOwner(OwnerLoginRequest request) {
        final Owner loginOwner = ownerSearchRepository.searchByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        loginOwner.validateEncodePasswordMatch(request.getPassword(), passwordEncoder);
        final String[] tokens = generateToken(loginOwner);
        loginOwner.updateRefreshToken(tokens[1]);
        return OwnerLoginResponse.builder()
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    private void isEmail(String email) {
        if (ownerRepository.existsByEmail(email)) {
            throw new DuplicatedException(ErrorResult.DUPLICATED_EMAIL);
        }
    }

    private void validatePasswordMatch(String password, String checkPassword) {
        if (!password.equals(checkPassword)) {
            throw new InvalidPasswordException(ErrorResult.INVALID_PASSWORD);
        }
    }

    private String[] generateToken(Owner owner) {
        final String accessToken = jwtService.createAccessToken(owner.getEmail());
        final String refreshToken = jwtService.createRefreshToken();
        return new String[]{accessToken, refreshToken};
    }
}
