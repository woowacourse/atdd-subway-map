package wooteco.subway.section.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.line.LineService;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.station.StationDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SectionApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("구간 등록 - 성공(상행종점 등록)")
    void createSection_success_up() throws Exception {
        // given
        Station upStation = stationDao.save(Station.from("잠실역"));
        Station downStation = stationDao.save(Station.from("잠실새내역"));
        Station newStation = stationDao.save(Station.from("강남역"));
        Line line = lineService.createLine("1호선", "bg-blue-300", upStation, downStation, 10);

        SectionRequest sectionRequest = new SectionRequest(newStation.getId(), upStation.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    @DisplayName("구간 등록 - 성공(하행종점 등록)")
    void createSection_success_down() throws Exception {
        // given
        Station upStation = stationDao.save(Station.from("잠실역"));
        Station downStation = stationDao.save(Station.from("잠실새내역"));
        Station newStation = stationDao.save(Station.from("강남역"));
        Line line = lineService.createLine("1호선", "bg-blue-300", upStation, downStation, 10);

        SectionRequest sectionRequest = new SectionRequest(downStation.getId(), newStation.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    @DisplayName("구간 등록 - 성공(상행기준 중간구간 구간 등록)")
    void createSection_success_middle_up() throws Exception {
        // given
        Station upStation = stationDao.save(Station.from("잠실역"));
        Station downStation = stationDao.save(Station.from("잠실새내역"));
        Station newStation = stationDao.save(Station.from("강남역"));
        Line line = lineService.createLine("1호선", "bg-blue-300", upStation, downStation, 10);

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), newStation.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    @Test
    @DisplayName("구간 등록 - 성공(하행기준 중간구간 구간 등록)")
    void createSection_success_middle_down() throws Exception {
        // given
        Station upStation = stationDao.save(Station.from("잠실역"));
        Station downStation = stationDao.save(Station.from("잠실새내역"));
        Station newStation = stationDao.save(Station.from("강남역"));
        Line line = lineService.createLine("1호선", "bg-blue-300", upStation, downStation, 10);

        SectionRequest sectionRequest = new SectionRequest(newStation.getId(), downStation.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

    private ResultActions 구간_추가(SectionRequest sectionRequest, Long lineId) throws Exception {
        return mockMvc.perform(post("/lines/" + lineId + "/sections")
                .content(objectMapper.writeValueAsString(sectionRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }
}