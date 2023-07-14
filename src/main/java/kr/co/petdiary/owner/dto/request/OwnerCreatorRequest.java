package kr.co.petdiary.owner.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.co.petdiary.global.common.Regexp;
import kr.co.petdiary.owner.entity.Owner;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OwnerCreatorRequest {
    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String checkPassword;

    @Pattern(regexp = Regexp.PHONE_PATTERN)
    private String cellPhone;

    public Owner toEntity() {
        return Owner.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .cellPhone(this.cellPhone)
                .build();
    }
}
