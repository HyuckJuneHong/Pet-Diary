package kr.co.petdiary.global.auth.jwt.service;

import kr.co.petdiary.global.auth.context.OwnerThreadLocal;
import kr.co.petdiary.global.error.exception.EntityNotFoundException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.repository.OwnerSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomLoginUserDetailsService implements UserDetailsService {
    private final OwnerSearchRepository ownerSearchRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Owner owner = ownerSearchRepository.searchByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorResult.NOT_FOUND_OWNER));
        OwnerThreadLocal.setOwner(owner);
        return User.builder()
                .username(owner.getEmail())
                .password(owner.getPassword())
                .roles(owner.getRole().name())
                .build();
    }
}
