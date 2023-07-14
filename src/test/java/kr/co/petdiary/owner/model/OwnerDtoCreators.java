package kr.co.petdiary.owner.model;

import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.entity.Owner;

public class OwnerDtoCreators {
    public static OwnerCreatorRequest ownerCreatorRequest() {
        return OwnerCreatorRequest.builder()
                .email("testEmail@naver.com")
                .name("testName")
                .password("testPassword")
                .cellPhone("010-1233-1233")
                .checkPassword("testPassword")
                .build();
    }

    public static OwnerCreatorRequest ownerCreatorRequest(String checkPassword) {
        return OwnerCreatorRequest.builder()
                .email("testEmail@naver.com")
                .name("testName")
                .password("testPassword")
                .cellPhone("010-1233-1233")
                .checkPassword(checkPassword)
                .build();
    }

    public static OwnerCreatorRequest ownerCreatorRequest(Owner owner) {
        return OwnerCreatorRequest.builder()
                .email(owner.getEmail())
                .name(owner.getName())
                .password(owner.getPassword())
                .checkPassword(owner.getPassword())
                .cellPhone(owner.getCellPhone())
                .build();
    }
}
