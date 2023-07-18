package kr.co.petdiary.owner.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.petdiary.global.util.DynamicQuery;
import kr.co.petdiary.owner.entity.Owner;
import kr.co.petdiary.owner.entity.QOwner;
import kr.co.petdiary.owner.model.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OwnerSearchRepository {
    private final JPAQueryFactory queryFactory;
    private final QOwner qOwner = QOwner.owner;

    public Optional<Owner> searchByEmail(String email) {
        return Optional.ofNullable(queryFactory.selectFrom(qOwner)
                .where(DynamicQuery.generateEq(email, qOwner.email::eq))
                .fetchOne());
    }

    public Optional<Owner> searchByRefreshToken(String refreshToken) {
        return Optional.ofNullable(queryFactory.selectFrom(qOwner)
                .where(DynamicQuery.generateEq(refreshToken, qOwner.refreshToken::eq))
                .fetchOne());
    }

    public Optional<Owner> searchBySocialIdAndLoginType(String socialId, LoginType loginType) {
        return Optional.ofNullable(queryFactory.selectFrom(qOwner)
                .where(DynamicQuery.generateEq(socialId, qOwner.socialId::eq),
                        DynamicQuery.generateEq(loginType, qOwner.loginType::eq))
                .fetchOne());
    }
}
