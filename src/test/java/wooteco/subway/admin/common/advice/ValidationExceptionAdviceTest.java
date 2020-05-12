package wooteco.subway.admin.common.advice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.CharEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import wooteco.subway.admin.common.advice.dto.DefaultExceptionResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ValidationExceptionAdviceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter(CharEncoding.UTF_8, true))
                .alwaysDo(print())
                .build();
    }


    @DisplayName("컨트롤러 매개변수가 Valid 규약에 걸리면 400과 필드, 메세지를 반환한다.")
    @Test
    void name() throws Exception {
        //when
        MvcResult mvcResult = mockMvc.perform(post("http://localhost:8080/lines")
                .content("{}")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(CharEncoding.UTF_8))
                .andExpect(status().isBadRequest())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();


        ObjectMapper objectMapper = new ObjectMapper();
        List<DefaultExceptionResponse<String>> defaultExceptionResponses = objectMapper.readValue(contentAsString, new TypeReference<List<DefaultExceptionResponse<String>>>() {
        });

        //then
        assertThat(defaultExceptionResponses).hasSize(4);
    }

}
