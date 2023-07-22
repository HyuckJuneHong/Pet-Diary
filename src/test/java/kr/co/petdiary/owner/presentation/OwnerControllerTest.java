package kr.co.petdiary.owner.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.petdiary.owner.application.OwnerService;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.request.OwnerLoginRequest;
import kr.co.petdiary.owner.dto.response.OwnerLoginResponse;
import kr.co.petdiary.owner.model.OwnerDtoCreators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerControllerTest {
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.access.header}")
    private String ACCESS_HEADER;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OwnerService ownerService;

    private OwnerCreatorRequest request;
    private OwnerCreatorRequest invalidRequest;
    private OwnerLoginRequest loginRequest;

    @BeforeEach
    void beforeEach() {
        request = OwnerDtoCreators.ownerCreatorRequest();
        invalidRequest = OwnerDtoCreators.invalidOwnerCreatorRequest();
        loginRequest = OwnerDtoCreators.ownerLoginRequest(request.getEmail(), request.getPassword());
    }

    @Test
    void 잘못된_DTO_필드로_반려인_등록_API_요청() throws Exception {
        //when, then
        mockMvc.perform(post("/api/v1/owners/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationExceptions.size()").value(5));
    }

    @Test
    void 반려인_등록_API() throws Exception {
        //when, then
        mockMvc.perform(post("/api/v1/owners/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.ownerId").exists())
                .andExpect(jsonPath("$.createAt").exists())
                .andExpect(jsonPath("$.name").value(request.getName()));
    }

    @Test
    void 중복된_반려인_등록_API() throws Exception {
        //given
        ownerService.createOwner(request);

        //when, then
        mockMvc.perform(post("/api/v1/owners/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("중복된 이메일 입니다."));
    }

    @Test
    void 로그인_성공_API() throws Exception {
        //given
        ownerService.createOwner(request);

        //when, then
        mockMvc.perform(post("/api/v1/owners/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void 비밀번호가_잘못된_로그인_InvalidPasswordException_API() throws Exception {
        //given
        ownerService.createOwner(request);
        final OwnerLoginRequest loginRequest
                = OwnerDtoCreators.ownerLoginRequest(request.getEmail(), "InvalidPassword");

        //when, then
        mockMvc.perform(post("/api/v1/owners/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }

    @Test
    void 존재하지_않는_Owner_로그인_InvalidPasswordException_API() throws Exception {
        //given
        final OwnerLoginRequest loginRequest
                = OwnerDtoCreators.ownerLoginRequest("NotFound@email.com", request.getPassword());
        ownerService.createOwner(request);

        //when, then
        mockMvc.perform(post("/api/v1/owners/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("반려인을 찾을 수 없습니다."));
    }

    @Test
    void 권한이_있는_유저의_URI_접근_인가_처리_API() throws Exception {
        //given
        ownerService.createOwner(request);
        final OwnerLoginResponse response = ownerService.loginOwner(loginRequest);

        //when, then
        mockMvc.perform(get("/api/v1/owners/jwt-test")
                        .header(ACCESS_HEADER, BEARER_PREFIX + response.accessToken())
                        .header(REFRESH_HEADER, BEARER_PREFIX + response.refreshToken()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().string("인가 테스트"));
    }

    @Test
    void 토큰이_만료되어_재발급이_필요한_유저의_URI_접근_인가_처리_API() throws Exception {
        //given
        ownerService.createOwner(request);
        final OwnerLoginResponse response = ownerService.loginOwner(loginRequest);

        //when, then
        mockMvc.perform(get("/api/v1/owners/jwt-test")
                        .header(ACCESS_HEADER, BEARER_PREFIX + "ExpiredAccessToken")
                        .header(REFRESH_HEADER, BEARER_PREFIX + response.refreshToken()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().string("인가 테스트"));
    }

    @Test
    void AccessToken만_가지고_있는_유저의_URI_접근_인가_처리_API() throws Exception {
        //given
        ownerService.createOwner(request);
        final OwnerLoginResponse response = ownerService.loginOwner(loginRequest);

        //when, then
        mockMvc.perform(get("/api/v1/owners/jwt-test")
                        .header(ACCESS_HEADER, BEARER_PREFIX + response.accessToken())
                        .header(REFRESH_HEADER, BEARER_PREFIX + "InvalidRefreshToken"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().string("인가 테스트"));
    }

    @Test
    void 토큰은_있지만_권한이_없는_유저의_URI_접근_인가_처리_MalformedJwtTokenException_API() throws Exception {
        //given
        ownerService.createOwner(request);

        //when, then
        mockMvc.perform(get("/api/v1/owners/jwt-test")
                        .header(ACCESS_HEADER, BEARER_PREFIX + "ExpiredAccessToken")
                        .header(REFRESH_HEADER, BEARER_PREFIX + "InvalidRefreshToken"))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.message").value("JWT가 올바르게 구성되지 않아서 거부되었습니다."));
    }
}
