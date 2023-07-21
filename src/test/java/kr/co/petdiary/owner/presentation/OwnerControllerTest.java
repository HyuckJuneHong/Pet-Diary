package kr.co.petdiary.owner.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.petdiary.owner.dto.request.OwnerCreatorRequest;
import kr.co.petdiary.owner.dto.response.OwnerCreatorResponse;
import kr.co.petdiary.owner.model.OwnerDtoCreators;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 반려인_등록_API() throws Exception {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.ownerCreatorRequest();
        OwnerCreatorResponse response = OwnerDtoCreators.OwnerCreatorResponse(request);

        //then
        mockMvc.perform(post("/api/v1/owners/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.ownerId").value(response.ownerId()))
                .andExpect(jsonPath("$.name").value(response.name()));
    }

    @Test
    void 잘못된_DTO_필드로_반려인_등록_API_요청() throws Exception {
        //given
        OwnerCreatorRequest request = OwnerDtoCreators.invalidOwnerCreatorRequest();

        //when, then
        mockMvc.perform(post("/api/v1/owners/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationExceptions.size()").value(5));
    }
}
