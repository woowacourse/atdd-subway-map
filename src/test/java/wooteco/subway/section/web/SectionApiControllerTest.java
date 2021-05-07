package wooteco.subway.section.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.line.LineService;
import wooteco.subway.section.SectionDao;
import wooteco.subway.station.StationDao;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SectionApiControllerTest {

    private static final String DOWN_STATION_NAME = "잠실새내역";
    private static final String UP_STATION_NAME = "잠실역";
    private static final int ORIGINAL_DISTANCE = 10;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineService lineService;
    @Autowired
    private SectionDao sectionDao;

    @Test
    @DisplayName("구간 등록 - 성공(상행종점 등록)")
    void createSection_success_up() throws Exception {
        final Station upStation = upStation();
        final Station downStation = downStation();
        Line line = createLine(upStation, downStation);

        Station newStation = stationDao.save(Station.from("강남역"));

        SectionRequest sectionRequest = new SectionRequest(newStation.getId(), upStation.getId(),
            4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();
        assertThat(sections).hasSize(3);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행종점 등록)")
    void createSection_success_down() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Station newStation = stationDao.save(Station.from("강남역"));

        Line line = createLine(upStation, downStation);

        SectionRequest sectionRequest = new SectionRequest(downStation.getId(),
            newStation.getId(),
            4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();
        assertThat(sections).hasSize(3);
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행기준 중간구간 구간 등록)")
    void createSection_success_middle_up() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Station newStation = stationDao.save(Station.from("강남역"));

        Line line = createLine(upStation, downStation);

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), newStation.getId(),
            4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();
        assertThat(sections).hasSize(3);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행기준 중간구간 구간 등록)")
    void createSection_success_middle_down() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Station newStation = stationDao.save(Station.from("강남역"));

        Line line = createLine(upStation, downStation);

        SectionRequest sectionRequest = new SectionRequest(newStation.getId(),
            downStation.getId(),
            4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"));

        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();
        assertThat(sections).hasSize(3);
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로 추가할 거리가 기존 거리보다 같거나 큰 경우)")
    void createSection_fail_overDistance() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Station newStation = stationDao.save(Station.from("강남역"));
        final Line line = createLine(upStation, downStation);

        final SectionRequest sectionRequest =
            new SectionRequest(newStation.getId(), downStation.getId(), ORIGINAL_DISTANCE);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("새로 추가할 거리가 기존 거리보다 겉거나 큽니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(존재하지 않는 역을 등록할 경우)")
    void createSection_fail_notExistStation() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        final Line line = createLine(upStation, downStation);

        final SectionRequest sectionRequest =
            new SectionRequest(Long.MAX_VALUE, downStation.getId(), ORIGINAL_DISTANCE);

        // when
        final ResultActions result = 구간_추가(sectionRequest, line.getId());

        //then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("존재하지 않는 역입니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(자연수가 아닌 거리를 등록할 경우)")
    void createSection_fail_notNumberDistance() throws Exception {
        // given
        final Station upStation = upStation();
        final Station station = downStation();
        final Station newStation = stationDao.save(Station.from("강남역"));
        final Line line = createLine(upStation, station);

        final SectionRequest sectionRequest =
            new SectionRequest(newStation.getId(), station.getId(), 0);
        
        // when
        final ResultActions result = 구간_추가(sectionRequest, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("자연수가 아닌 거리를 등록하셨습니다."));
    }
    

    @Test
    @DisplayName("구간 등록 - 실패(의미상 중복된 섹션을 등록할 경우)")
    void createSection_fail_duplicatedSection() throws Exception {
        //given
        final Station upStation = upStation();
        final Station downStation = downStation();
        final Line line = createLine(upStation, downStation);
        final SectionRequest sectionRequest = 
            new SectionRequest(downStation.getId(), upStation.getId(), 3);
        
        //when
        ResultActions result = 구간_추가(sectionRequest, line.getId());
        
        //then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("중복된 섹션입니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로운 섹션의 두 역이 모두 같은 노선에 포함된 경우)")
    void createSection_fail_existStations() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        final Station 강남역 = Station.from("강남역");
        final Line line = createLine(upStation, 강남역);

        final SectionRequest sectionRequest1 =
            new SectionRequest(강남역.getId(), downStation.getId(), 3);

        final SectionRequest sectionRequest2 =
            new SectionRequest(upStation.getId(), downStation.getId(), 3);

        구간_추가(sectionRequest1, line.getId());

        // when
        final ResultActions result = 구간_추가(sectionRequest2, line.getId());

        // then
        result.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().string("두 역이 모두 같은 노선에 포함되었습니다."));
    }

    private ResultActions 구간_추가(SectionRequest sectionRequest, Long lineId) throws Exception {
        return mockMvc.perform(post("/lines/" + lineId + "/sections")
            .content(objectMapper.writeValueAsString(sectionRequest))
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    private Line createLine(Station upStation, Station downStation) {
        return lineService.createLine("1호선", "bg-blue-300", upStation, downStation, ORIGINAL_DISTANCE);
    }

    private Station downStation() {
        return stationDao.save(Station.from(DOWN_STATION_NAME));
    }

    private Station upStation() {
        return stationDao.save(Station.from(UP_STATION_NAME));
    }
}