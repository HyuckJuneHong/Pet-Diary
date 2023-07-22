package kr.co.petdiary.owner.model;

import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.request.OwnerLoginRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.entity.Owner;

import java.time.LocalDateTime;

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

    public static OwnerLoginRequest ownerLoginRequest(String email, String password) {
        return OwnerLoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static OwnerCreatorResponse OwnerCreatorResponse(OwnerCreatorRequest request) {
        return OwnerCreatorResponse.builder()
                .ownerId(1L)
                .createAt(LocalDateTime.now())
                .name(request.getName())
                .build();
    }

    public static OwnerCreatorRequest invalidOwnerCreatorRequest() {
        return OwnerCreatorRequest.builder()
                .email("@naver.com")
                .name(" ")
                .password(" ")
                .cellPhone("010-1-1")
                .checkPassword(" ")
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
