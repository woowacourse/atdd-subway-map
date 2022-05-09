package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.StationService;

@WebMvcTest(StationController.class)
public class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private StationService stationService;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() throws Exception {
        // given
        StationRequest stationRequest = new StationRequest("test");
        StationResponse stationResponse = new StationResponse(1L, "test");
        given(stationService.createStation(any(StationRequest.class)))
                .willReturn(stationResponse);
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationRequest)));
        // then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("test"))
                .andExpect(header().stringValues("Location", "/stations/1"));
    }

    @DisplayName("지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createStation_duplication_exception() throws Exception {
        // given
        StationRequest test = new StationRequest("test");
        given(stationService.createStation(any()))
                .willThrow(new IllegalArgumentException("중복되는 지하철역이 존재합니다."));
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isBadRequest());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() throws Exception {
        // given
        given(stationService.showStations())
                .willReturn(List.of(new StationResponse(1L, "test1"), new StationResponse(2L, "test2")));
        // when
        ResultActions perform = mockMvc.perform(get("/stations"));
        // then
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("test2"));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() throws Exception {
        // given
        doNothing()
                .when(stationService)
                .deleteStation(any());
        // when
        ResultActions perform = mockMvc.perform(delete("/stations/1"));
        // then
        perform.andExpect(status().isNoContent());
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철역이 없다면 에러를 응답한다.")
    @Test
    void deleteStation_noExistStation_exception() throws Exception {
        // given
        doThrow(new IllegalArgumentException("해당 ID의 지하철역이 존재하지 않습니다."))
                .when(stationService)
                .deleteStation(any());
        // when
        ResultActions perform = mockMvc.perform(delete("/stations/1"));
        // then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("해당 ID의 지하철역이 존재하지 않습니다."));
    }
}