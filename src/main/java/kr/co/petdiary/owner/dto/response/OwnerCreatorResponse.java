package kr.co.petdiary.owner.dto.response;


import kr.co.petdiary.owner.entity.Owner;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OwnerCreatorResponse(
        Long ownerId,
        String name,
        LocalDateTime createAt
) {
    public static OwnerCreatorResponse toDto(Owner owner) {
        return OwnerCreatorResponse.builder()
                .ownerId(owner.getId())
                .name(owner.getName())
                .createAt(owner.getCreatedAt())
                .build();
    }
}
