package kr.co.petdiary.owner.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
