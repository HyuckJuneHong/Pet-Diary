package kr.co.petdiary.owner.presentation;

import kr.co.petdiary.owner.application.OwnerService;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.request.OwnerLoginRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.dto.response.OwnerLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/owners")
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping("/register")
    public ResponseEntity<OwnerCreatorResponse> createOwner(@RequestBody @Validated OwnerCreatorRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ownerService.createOwner(request));
    }

    @PostMapping("/login")
    public ResponseEntity<OwnerLoginResponse> loginOwner(@RequestBody @Validated OwnerLoginRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ownerService.loginOwner(request));
    }

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "인가 테스트";
    }
}
