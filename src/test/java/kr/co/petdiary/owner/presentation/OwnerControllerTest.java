package kr.co.petdiary.owner.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.petdiary.owner.application.OwnerService;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.model.OwnerDtoCreators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OwnerService ownerService;

    @Test
    void 반려인_등록() throws Exception {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest();
        OwnerCreatorResponse response = OwnerDtoCreators.OwnerCreatorResponse(request);

        //when
        when(ownerService.createOwner(any(OwnerCreatorRequest.class))).thenReturn(response);

        //then
        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(content().string(objectMapper.writeValueAsString(response)));
    }

    @Test
    void 모든_DTO필드_잘못된_요청() throws Exception {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.invalidOwnerCreatorRequest();

        //when, then
        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationExceptions.size()").value(5));
    }
}
