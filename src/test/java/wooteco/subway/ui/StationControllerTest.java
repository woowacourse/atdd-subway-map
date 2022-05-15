package wooteco.subway.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wooteco.subway.Fixture.강남역;
import static wooteco.subway.Fixture.청계산입구역;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.StationService;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StationService stationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("역을 생성한다.")
    void createStation() throws Exception {
        final StationRequest stationRequest = new StationRequest(강남역.getName());

        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(강남역.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("중복된 이름으로 역 생성 요청 - 400 반환")
    void createStationWithDuplicateName() throws Exception {
        final StationRequest stationRequest = new StationRequest(강남역.getName());
        stationService.createStation(stationRequest.getName());

        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stationRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void showStations() throws Exception {
        stationService.createStation(강남역.getName());
        stationService.createStation(청계산입구역.getName());

        mockMvc.perform(get("/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(강남역.getName()))
                .andExpect(jsonPath("$[1].name").value(청계산입구역.getName()));
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void deleteStation() throws Exception {
        final Station station = stationService.createStation(강남역.getName());

        mockMvc.perform(delete("/stations/" + station.getId()))
                .andExpect(status().isNoContent());
    }


}
