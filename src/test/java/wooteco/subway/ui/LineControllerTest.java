package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wooteco.subway.Fixtures.BLUE;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.LINE_4;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SINSA;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.service.LineService;

@Nested
@WebMvcTest(LineController.class)
public class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineService lineService;

    @Test
    @DisplayName("지하철 노선을 생성한다. 이때 관련 구간을 같이 생성한다.")
    void create() throws Exception {
        // given
        final CreateLineRequest request = new CreateLineRequest(LINE_2, RED, 1L, 2L, 10);
        final String requestContent = objectMapper.writeValueAsString(request);

        final List<StationResponse> stations = List.of(new StationResponse(1L, HYEHWA),
                new StationResponse(2L, SINSA));
        final LineResponse response = new LineResponse(1L, LINE_2, RED, stations);

        // mocking
        given(lineService.create(any(CreateLineRequest.class))).willReturn(response);

        // when
        mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                // then
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/lines/1"))
                .andExpect(jsonPath("name").value(LINE_2))
                .andExpect(jsonPath("color").value(RED))
                .andExpect(jsonPath("stations[0].id").value(1L))
                .andExpect(jsonPath("stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("stations[1].id").value(2L))
                .andExpect(jsonPath("stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다. 관련 역들도 함께 조회한다.")
    void showAll() throws Exception {
        // given
        final List<LineResponse> response = List.of(
                new LineResponse(1L, LINE_2, RED,
                        List.of(new StationResponse(1L, HYEHWA), new StationResponse(2L, SINSA))),
                new LineResponse(2L, LINE_4, BLUE,
                        List.of(new StationResponse(1L, HYEHWA), new StationResponse(2L, SINSA))));

        // mocking
        given(lineService.showAll()).willReturn(response);

        // when
        mockMvc.perform(get("/lines/"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value(LINE_2))
                .andExpect(jsonPath("$[0].color").value(RED))
                .andExpect(jsonPath("$[0].stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("$[0].stations[1].name").value(SINSA))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value(LINE_4))
                .andExpect(jsonPath("$[1].color").value(BLUE))
                .andExpect(jsonPath("$[1].stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("$[1].stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("지하철 노선 ID를 이용해 노선을 조회한다. 관련 역들도 함께 조회한다.")
    void show() throws Exception {
        // given
        final List<StationResponse> stations = List.of(new StationResponse(1L, HYEHWA),
                new StationResponse(2L, SINSA));
        final LineResponse response = new LineResponse(1L, LINE_2, RED, stations);

        // mocking
        given(lineService.show(1L)).willReturn(response);

        // when
        mockMvc.perform(get("/lines/1"))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(LINE_2))
                .andExpect(jsonPath("color").value(RED))
                .andExpect(jsonPath("stations[0].id").value(1L))
                .andExpect(jsonPath("stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("stations[1].id").value(2L))
                .andExpect(jsonPath("stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("지하철 구간을 등록한다. - 성공 200")
    void createSection_ok() throws Exception {
        // given
        final long lineId = 1L;
        final CreateSectionRequest request = new CreateSectionRequest(2L, 3L, 5);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/lines/" + lineId + "/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지하철 구간을 삭제한다. - 성공 200")
    void deleteSection_ok() throws Exception {
        // given
        final long lineId = 1L;
        final long stationId = 1L;

        // when
        mockMvc.perform(delete("/lines/" + lineId + "/sections?stationId=" + stationId))
                // then
                .andExpect(status().isOk());
    }
}
