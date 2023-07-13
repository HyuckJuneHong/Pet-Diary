package kr.co.petdiary.owner.entity;

import jakarta.persistence.*;
import kr.co.petdiary.global.common.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_owners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "owner_id", length = 4))
public class Owner extends BaseEntity {
}
