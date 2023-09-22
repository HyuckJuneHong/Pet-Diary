package kr.co.petdiary.owner.dto.response;

import lombok.Builder;

@Builder
public record OwnerLoginResponse(
        String accessToken,
        String refreshToken
) {
}
