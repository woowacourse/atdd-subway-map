package wooteco.subway.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.LineService;
import wooteco.subway.application.StationService;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql({"/schema.sql", "/test-data.sql"})
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNewLine() throws Exception {
        final Station upStation = stationService.findStationById(1L);
        final Station downStation = stationService.findStationById(2L);
        final String name = "경의선";
        final String color = "푸른이";
        final LineRequest lineRequest = new LineRequest(name, color, upStation.getId(), downStation.getId(), 10);

        mockMvc.perform(post("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lineRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(name))
                .andExpect(jsonPath("color").value(color))
                .andExpect(jsonPath("stations[0].name").value(upStation.getName()))
                .andExpect(jsonPath("stations[1].name").value(downStation.getName()))
                .andDo(print());
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void showLines() throws Exception {
        mockMvc.perform(get("/lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("9호선"))
                .andExpect(jsonPath("$[1].name").value("신분당선"))
                .andExpect(jsonPath("$[2].name").value("경의중앙선"))
                .andDo(print());
    }

    @Test
    @DisplayName("노선 한 개를 조회한다.")
    void showLine() throws Exception {
        mockMvc.perform(get("/lines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("9호선"))
                .andExpect(jsonPath("stations[0].name").value("삼전역"))
                .andExpect(jsonPath("stations[1].name").value("잠실역"));
    }

    @Test
    @DisplayName("노선의 정보를 변경한다.")
    void modifyLine() throws Exception {
        final String name = "새로운노선";
        final String color = "새로운색깔";
        final LineRequest lineRequest = new LineRequest(name, color, 1L, 2L, 10);

        mockMvc.perform(put("/lines/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lineRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() throws Exception {
        mockMvc.perform(delete("/lines/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() throws Exception {
        final Station 잠실 = stationService.findStationById(2L);
        final Station 석촌 = stationService.findStationById(3L);
        final SectionRequest sectionRequest = new SectionRequest(잠실.getId(), 석촌.getId(), 10);

        mockMvc.perform(post("/lines/1/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sectionRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() throws Exception {
        final Station 잠실 = stationService.findStationById(2L);
        final Station 석촌 = stationService.findStationById(3L);
        lineService.addSection(1L, 잠실.getId(), 석촌.getId(), 10);

        mockMvc.perform(delete("/lines/1/sections")
                .param("stationId", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("구간이 1개인 상태에서 구간의 역을 삭제하면 400에러가 발생한다.")
    void deleteFinalSection() throws Exception {
        mockMvc.perform(delete("/lines/1/sections")
                .param("stationId", "1"))
                .andExpect(status().isBadRequest());
    }
}
