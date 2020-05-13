package wooteco.subway.admin.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ValidationExceptionAdviceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter(new CharacterEncodingFilter(CharEncoding.UTF_8, true))
            .alwaysDo(print())
            .build();
    }

    @DisplayName("컨트롤러 매개변수가 Valid 규약에 걸리면 400 Bad Request를 반환")
    @Test
    void whenInputIsInvalid_thenReturnState400() throws Exception {
        String body = objectMapper.writeValueAsString("{}");
        //when
        mockMvc.perform(post("/lines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
            .characterEncoding(CharEncoding.UTF_8))
            .andExpect(status().isBadRequest());
    }
}

