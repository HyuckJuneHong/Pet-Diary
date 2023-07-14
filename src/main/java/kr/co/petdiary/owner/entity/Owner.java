package kr.co.petdiary.owner.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import kr.co.petdiary.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tbl_owners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "owner_id", length = 4))
public class Owner extends BaseEntity {
    @Email
    @Column(name = "email", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$")
    @Column(name = "cell_phone", length = 20, nullable = false, unique = true)
    private String cellPhone;

    @Builder
    private Owner(String email, String password, String name, String cellPhone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.cellPhone = cellPhone;
    }
}
