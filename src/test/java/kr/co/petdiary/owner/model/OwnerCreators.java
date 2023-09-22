package kr.co.petdiary.owner.model;

import kr.co.petdiary.owner.entity.Owner;

public class OwnerCreators {
    public static Owner createOwner() {
        return Owner.builder()
                .email("testEmail@naver.com")
                .name("testName")
                .password("testPassword")
                .cellPhone("010-1233-1233")
                .role(Role.USER)
                .build();
    }

    public static Owner createOwnerByCellPhone(String cellPhone) {
        return Owner.builder()
                .email("testEmail@naver.com")
                .name("testName")
                .password("testPassword")
                .cellPhone(cellPhone)
                .role(Role.USER)
                .build();
    }

    public static Owner createOwnerByEmail(String email) {
        return Owner.builder()
                .email(email)
                .name("testName")
                .password("testPassword")
                .cellPhone("010-1234-5678")
                .role(Role.USER)
                .build();
    }
}
