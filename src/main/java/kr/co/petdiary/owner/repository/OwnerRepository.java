package kr.co.petdiary.owner.repository;

import kr.co.petdiary.owner.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
