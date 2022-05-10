package wooteco.subway.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationService;

@WebMvcTest(StationController.class)
public class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StationService stationService;

    private final StationRequest romaRequest = new StationRequest("roma");
    private final Station romaStation = new Station(1L, "roma");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() throws Exception {
        // given
        given(stationService.save(any(StationRequest.class)))
                .willReturn(new StationResponse(romaStation));
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(romaRequest)));
        // then
        perform.andExpectAll(
                status().isCreated(),
                jsonPath("id").value(1),
                jsonPath("name").value("roma"),
                header().stringValues("Location", "/stations/1")
        );
    }

    @DisplayName("지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createStation_duplication_exception() throws Exception {
        // given
        given(stationService.save(any(StationRequest.class)))
                .willThrow(new IllegalArgumentException("roma : 중복되는 지하철역이 존재합니다."));
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(romaRequest)));
        // then
        perform.andExpectAll(
                status().isBadRequest(),
                jsonPath("message").value("roma : 중복되는 지하철역이 존재합니다.")
        );
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() throws Exception {
        // given
        Station brownStation = new Station(2L, "brown");
        given(stationService.findAll())
                .willReturn(List.of(new StationResponse(romaStation), new StationResponse(brownStation)));
        // when
        ResultActions perform = mockMvc.perform(get("/stations"));
        // then
        perform.andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.length()").value(2),
                jsonPath("$[0].id").value(1),
                jsonPath("$[0].name").value("roma"),
                jsonPath("$[1].id").value(2),
                jsonPath("$[1].name").value("brown")
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() throws Exception {
        // when
        ResultActions perform = mockMvc.perform(delete("/stations/1"));
        // then
        assertAll(
                () -> verify(stationService).delete(1L),
                () -> perform.andExpect(status().isNoContent())
        );
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철역이 없다면 에러를 응답한다.")
    @Test
    void deleteStation_noExistStation_exception() throws Exception {
        // given
        given(stationService.delete(anyLong()))
                .willThrow(new IllegalArgumentException("1 : 해당 ID의 지하철역이 존재하지 않습니다."));
        // when
        ResultActions perform = mockMvc.perform(delete("/stations/1"));
        // then
        perform.andExpectAll(
                status().isBadRequest(),
                jsonPath("message").value("1 : 해당 ID의 지하철역이 존재하지 않습니다.")
        );
    }
}
