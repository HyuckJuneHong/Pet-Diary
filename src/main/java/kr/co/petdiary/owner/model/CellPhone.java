package kr.co.petdiary.owner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CellPhone {
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$")
    @Column(name = "cell_phone", length = 20, nullable = false, unique = true)
    private String cellPhone;
}
