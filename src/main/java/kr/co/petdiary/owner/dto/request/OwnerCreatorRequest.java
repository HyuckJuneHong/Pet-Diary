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
    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    @Email(message = "이메일을 형식에 맞게 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;

    @NotBlank(message = "확인 비밀번호를 입력하세요.")
    private String checkPassword;

    @Pattern(regexp = Regexp.PHONE_PATTERN, message = "전화번호를 형식에 맞게 입력하세요.")
    private String cellPhone;

    public Owner toOwner() {
        return Owner.builder()
                .email(this.email)
                .name(this.name)
                .password(this.password)
                .cellPhone(this.cellPhone)
                .build();
    }
}
