package wooteco.subway.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.ClassAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;


import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LineController.class)
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LineService lineService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() throws Exception {
        List<StationResponse> stations = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineRequest lineRequest = new LineRequest("2호선", "green", 1L, 2L, 10);
        LineResponse lineResponse = new LineResponse(1L, lineRequest.getName(), lineRequest.getColor(), stations);
        given(lineService.create(any(LineRequest.class)))
                .willReturn(lineResponse);

        MockHttpServletResponse result = mockMvc.perform(
                post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lineRequest)))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("생성하려는 노선의 이름이 비어있으면 예외를 반환한다.")
    void createLine_NullNameException() throws Exception {
        LineRequest lineRequest = new LineRequest(null, "green", 1L, 2L, 10);
        given(lineService.create(lineRequest)).willReturn(null);

        mockMvc.perform(
                post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lineRequest)))
                .andExpect(this::checkException)
                .andReturn();
    }

    @Test
    @DisplayName("생성하려는 노선의 색깔이 비어있으면 예외를 반환한다.")
    void createLine_NullColorException() throws Exception {
        LineRequest lineRequest = new LineRequest("2호선", null, 1L, 2L, 10);
        given(lineService.create(lineRequest)).willReturn(null);

        mockMvc.perform(
                post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lineRequest)))
                .andExpect(this::checkException)
                .andReturn();
    }

    @Test
    void findAllLine() {
    }

    @Test
    void findLineById() {
    }

    @Test
    void updateLine() {
    }

    @Test
    void delete() {
    }

    private ClassAssert checkException(MvcResult exception) {
        return assertThat(exception.getResolvedException().getClass()).isAssignableFrom(MethodArgumentNotValidException.class);
    }
}
