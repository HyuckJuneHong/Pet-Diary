package kr.co.petdiary.owner.model;

import kr.co.petdiary.owner.entity.Owner;

public class OwnerCreators {
    public static Owner createOwner(String cellPhone) {
        return Owner.builder()
                .email("example@naver.com")
                .name("testName")
                .password("testPassword")
                .cellPhone(cellPhone)
                .build();
    }
}
