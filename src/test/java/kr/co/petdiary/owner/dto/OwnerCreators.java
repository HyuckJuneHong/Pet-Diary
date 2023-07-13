package kr.co.petdiary.owner.dto;

import kr.co.petdiary.owner.entity.Owner;

public class OwnerCreators {
    public static Owner createOwner() {
        return Owner.builder()
                .email("example@naver.com")
                .name("testName")
                .password("testPassword")
                .cellphone("010-1111-1111")
                .build();
    }
}
