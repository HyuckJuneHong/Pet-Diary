package kr.co.petdiary.owner.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Column(name = "cellphone", length = 20, nullable = false, unique = true)
    private String cellphone;

    @Builder
    private Owner(String email, String password, String name, String cellphone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.cellphone = cellphone;
    }
}
