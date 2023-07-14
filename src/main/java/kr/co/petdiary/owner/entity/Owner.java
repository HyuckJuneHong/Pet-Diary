package kr.co.petdiary.owner.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import kr.co.petdiary.global.common.BaseEntity;
import kr.co.petdiary.owner.model.CellPhone;
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

    @Valid
    @Embedded
    private CellPhone cellPhone = new CellPhone();

    @Builder
    private Owner(String email, String password, String name, CellPhone cellPhone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.cellPhone = cellPhone;
    }
}
