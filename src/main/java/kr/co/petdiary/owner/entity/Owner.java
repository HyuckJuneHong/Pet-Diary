package kr.co.petdiary.owner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import kr.co.petdiary.global.common.BaseEntity;
import kr.co.petdiary.global.common.Regexp;
import kr.co.petdiary.global.error.exception.InvalidPasswordException;
import kr.co.petdiary.global.error.model.ErrorResult;
import kr.co.petdiary.owner.model.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
@Table(name = "tbl_owners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "owner_id", length = 4))
public class Owner extends BaseEntity {
    @Email
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Pattern(regexp = Regexp.PHONE_PATTERN)
    @Column(name = "cell_phone", length = 20, nullable = false, unique = true)
    private String cellPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private Role role;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Builder
    private Owner(String email, String password, String name, String cellPhone, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.cellPhone = cellPhone;
        this.role = role;
    }

    public String getRoleKey() {
        return this.getRole().getKey();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void validateEncodePasswordMatch(String password, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(password, this.password)) {
            throw new InvalidPasswordException(ErrorResult.INVALID_PASSWORD);
        }
    }
}
