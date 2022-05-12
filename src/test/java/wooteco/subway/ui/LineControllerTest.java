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
import org.springframework.web.bind.MethodArgumentNotValidException;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    @DisplayName("모든 노선을 반환한다.")
    void findAllLine() throws Exception {
        List<StationResponse> stations1 = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineRequest lineRequest1 = new LineRequest("2호선", "green", 1L, 2L, 10);
        LineResponse lineResponse1 = new LineResponse(1L, lineRequest1.getName(), lineRequest1.getColor(), stations1);

        List<StationResponse> stations2 = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineRequest lineRequest2 = new LineRequest("2호선", "green", 1L, 2L, 10);
        LineResponse lineResponse2 = new LineResponse(1L, lineRequest2.getName(), lineRequest2.getColor(), stations2);
        given(lineService.findAll()).willReturn(List.of(lineResponse1, lineResponse2));

        MockHttpServletResponse response = mockMvc.perform(
                get("/lines"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("id값이 일치하는 노선을 반환한다.")
    void findLineById() throws Exception {
        List<StationResponse> stations = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green", stations);
        given(lineService.findById(any(Long.class)))
                .willReturn(lineResponse);

        MockHttpServletResponse response = mockMvc.perform(
                get("/lines/1"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선의 정보를 변경한다.")
    void updateLine() throws Exception {
        List<StationResponse> stations = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green", stations);
        given(lineService.findById(any(Long.class)))
                .willReturn(lineResponse);
        LineRequest newLineRequest = new LineRequest("3호선", "yellow");

        MockHttpServletResponse response = mockMvc.perform(
                put("/lines/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLineRequest)))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() throws Exception {
        List<StationResponse> stations = List.of(
                new StationResponse(1L, "강남역"),
                new StationResponse(2L, "선릉역")
        );
        LineResponse lineResponse = new LineResponse(1L, "2호선", "green", stations);
        given(lineService.findById(any(Long.class)))
                .willReturn(lineResponse);

        MockHttpServletResponse response = mockMvc.perform(
                delete("/lines/1"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ClassAssert checkException(MvcResult exception) {
        return assertThat(exception.getResolvedException().getClass()).isAssignableFrom(MethodArgumentNotValidException.class);
    }
}
