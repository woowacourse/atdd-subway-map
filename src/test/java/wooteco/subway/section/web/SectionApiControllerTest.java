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
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.line.LineService;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SectionApiControllerTest {

    private static final String UP_STATION_NAME = "잠실역";
    private static final String DOWN_STATION_NAME = "잠실새내역";
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
        assertThat(sections).hasSize(2);
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
        assertThat(sections).hasSize(2);
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
        assertThat(sections).hasSize(2);
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
        assertThat(sections).hasSize(2);
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
                .andExpect(content().string("새로 추가할 거리가 기존 거리보다 같거나 큽니다."));
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
                .andExpect(content().string("해당 역이 존재하지 않습니다."));
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
                .andExpect(content().string("중복된 구간입니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로운 섹션의 두 역이 모두 같은 노선에 포함된 경우)")
    void createSection_fail_existStations() throws Exception {
        // given
        final Station upStation = upStation();
        final Station downStation = downStation();
        final Station dummyStation = stationDao.save(Station.from("강남역"));
        final Line line = createLine(upStation, dummyStation);

        final SectionRequest sectionRequest1 =
                new SectionRequest(dummyStation.getId(), downStation.getId(), 3);

        final SectionRequest sectionRequest2 =
                new SectionRequest(upStation.getId(), downStation.getId(), 3);

        구간_추가(sectionRequest1, line.getId());

        // when
        final ResultActions result = 구간_추가(sectionRequest2, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("잘못된 구간입니다."));
    }

    @Test
    @DisplayName("구간 제거 - 성공")
    void deleteSection_success() throws Exception {
        // given
        // 강남역 -> 잠실역 -> 잠실새네역
        final Station upStation = upStation();
        final Station downStation = downStation();
        Line line = createLine(upStation, downStation);

        Station newStation = stationDao.save(Station.from("강남역"));

        SectionRequest sectionRequest =
                new SectionRequest(newStation.getId(), upStation.getId(), 4);

        구간_추가(sectionRequest, line.getId());
        // when
        ResultActions result = mockMvc.perform(delete("/lines/" + line.getId() + "/sections?stationId=" + upStation.getId()));

        // then
        result.andDo(print())
                .andExpect(status().isNoContent());

        final Sections sections = sectionDao.findSectionsByLineId(line.getId());
        assertThat(sections.sections().size()).isEqualTo(1);
        assertThat(sections.sections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(sections.sections().get(0).getDownStation().getName()).isEqualTo("잠실새내역");
        assertThat(sections.sections().get(0).getDistance()).isEqualTo(14);
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선이 존재하지 않을 시)")
    public void deleteSection_fail_notExistLine() throws Exception {
        //given

        //when
        final ResultActions result = mockMvc.perform(delete("/lines/" + Long.MAX_VALUE + "/sections?stationId=1"));
        //then

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구간 제거 - 실패(역이 해당 노선에 등록되어 있지 않을 시)")
    public void deleteSection_fail_noStationInLine() throws Exception {
        //given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Line line = createLine(upStation, downStation);

        //when
        final ResultActions result = mockMvc.perform(
                delete("/lines/" + line.getId() + "/sections?stationId=" + Long.MAX_VALUE)
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선에 구간이 하나밖에 존재하지 않을 시)")
    public void deleteSection_fail_onlyOneSectionExist() throws Exception {
        //given
        final Station upStation = upStation();
        final Station downStation = downStation();
        Line line = createLine(upStation, downStation);

        //when
        final ResultActions result = mockMvc.perform(
                delete("/lines/" + line.getId() + "/sections?stationId=" + downStation.getId())
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("구간 등록 - 성공(중간구간 구간 등록 a-b-c-d -> a-b-k-c-d)")
    void createSection_success_middle() throws Exception {
        // given
        final Station 잠실역 = upStation();
        final Station 잠실새내역 = downStation();
        Station 강남역 = stationDao.save(Station.from("강남역"));
        Station 동탄역 = stationDao.save(Station.from("동탄역"));
        Station 수서역 = stationDao.save(Station.from("수서역"));
        Line line = createLine(잠실역, 잠실새내역);
        SectionRequest 강남_잠실 = new SectionRequest(강남역.getId(), 잠실역.getId(), 4);
        SectionRequest 잠실새내_동탄 = new SectionRequest(잠실새내역.getId(), 동탄역.getId(), 4);
        구간_추가(강남_잠실, line.getId());
        구간_추가(잠실새내_동탄, line.getId());
        // when
        SectionRequest 잠실_수서 = new SectionRequest(잠실역.getId(), 수서역.getId(), 2);
        ResultActions result = 구간_추가(잠실_수서, line.getId());
        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();
        assertThat(sections).hasSize(4);
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
