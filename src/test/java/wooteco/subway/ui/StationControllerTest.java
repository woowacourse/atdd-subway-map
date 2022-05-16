package wooteco.subway.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(StationController.class)
class StationControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StationService stationService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("역을 생성한다.")
    void createStation() throws Exception {
        StationRequest stationRequest = new StationRequest("강남역");

        given(stationService.create(any(StationRequest.class))).willReturn(new StationResponse(1L, "강남역"));

        MockHttpServletResponse result = mockMvc.perform(
                post("/stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stationRequest)))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("이름이 비어있으면 예외를 발생시킨다.")
    void createStation_Exception() throws Exception {
        StationRequest stationRequest = new StationRequest(null);

        given(stationService.create(any(StationRequest.class))).willReturn(new StationResponse(1L, "강남역"));

        mockMvc.perform(
                post("/stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stationRequest)))
                .andExpect(this::checkValidException);
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void deleteStation() throws Exception {
        MockHttpServletResponse result = mockMvc.perform(
                delete("/stations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
