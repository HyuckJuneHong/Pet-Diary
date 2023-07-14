package kr.co.petdiary.owner.presentation;

import kr.co.petdiary.owner.application.OwnerService;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping
    public ResponseEntity<OwnerCreatorResponse> createOwner(@RequestBody @Validated OwnerCreatorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ownerService.createOwner(request));
    }
}
