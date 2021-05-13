package wooteco.subway.section.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.ApiControllerTest;
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
@Transactional
@DisplayName("구간관련 테스트")
class SectionApiControllerTest extends ApiControllerTest {

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
    void create_성공_상행종점추가() throws Exception {
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Line line = 노선_생성(잠실역, 잠실새내역);

        Station 강남역 = stationDao.create(Station.create("강남역"));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 잠실역.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행종점 등록)")
    void create_성공_하행종점추가() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));

        Line line = 노선_생성(잠실역, 잠실새내역);

        SectionRequest sectionRequest = new SectionRequest(잠실새내역.getId(), 강남역.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행기준 중간구간 구간 등록)")
    void create_성공_중간역상행기준() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));

        Line line = 노선_생성(잠실역, 잠실새내역);

        SectionRequest sectionRequest = new SectionRequest(잠실역.getId(), 강남역.getId(), 4);

        // when
        ResultActions result = 구간_추가(sectionRequest, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행기준 중간구간 구간 등록)")
    void create_성공_중간하행기준() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));

        Line line = 노선_생성(잠실역, 잠실새내역);

        SectionRequest 강남_잠실새내 = new SectionRequest(강남역.getId(), 잠실새내역.getId(), 4);

        // when
        ResultActions result = 구간_추가(강남_잠실새내, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("구간 등록 - 성공(중간 구간 등록 a-b-c-d --(b-k)--> a-b-k-c-d)")
    void create_성공_중간앞() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));
        Station 동탄역 = stationDao.create(Station.create("동탄역"));
        Station 수서역 = stationDao.create(Station.create("수서역"));

        Line line = 노선_생성(잠실역, 잠실새내역);

        SectionRequest 강남_잠실 = new SectionRequest(강남역.getId(), 잠실역.getId(), 4);
        SectionRequest 잠실새내_동탄 = new SectionRequest(잠실새내역.getId(), 동탄역.getId(), 4);

        구간_추가(강남_잠실, line.getId());
        구간_추가(잠실새내_동탄, line.getId());

        // when
        SectionRequest 잠실_수서 = new SectionRequest(잠실역.getId(), 수서역.getId(), 2);
        ResultActions result = 구간_추가(잠실_수서, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("구간 등록 - 성공(중간 구간 등록 a-b-c-d --(k-c)--> a-b-k-c-d)")
    void create_성공_중간뒤() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));
        Station 동탄역 = stationDao.create(Station.create("동탄역"));
        Station 수서역 = stationDao.create(Station.create("수서역"));

        Line line = 노선_생성(잠실역, 잠실새내역);

        SectionRequest 강남_잠실 = new SectionRequest(강남역.getId(), 잠실역.getId(), 4);
        SectionRequest 잠실새내_동탄 = new SectionRequest(잠실새내역.getId(), 동탄역.getId(), 4);

        구간_추가(강남_잠실, line.getId());
        구간_추가(잠실새내_동탄, line.getId());

        // when
        SectionRequest 수서_잠실새내 = new SectionRequest(수서역.getId(), 잠실새내역.getId(), 2);
        ResultActions result = 구간_추가(수서_잠실새내, line.getId());
        final List<Section> sections = sectionDao.findSectionsByLineId(line.getId()).sections();

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        assertThat(sections).hasSize(4);
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로 추가할 거리가 기존 거리보다 같거나 큰 경우)")
    void create_실패_거리큼() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Station 강남역 = stationDao.create(Station.create("강남역"));
        final Line line = 노선_생성(잠실역, 잠실새내역);

        final SectionRequest 강남_잠실새내 =
                new SectionRequest(강남역.getId(), 잠실새내역.getId(), ORIGINAL_DISTANCE);

        // when
        ResultActions result = 구간_추가(강남_잠실새내, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("새로 추가할 거리가 기존 거리보다 같거나 큽니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(존재하지 않는 역을 등록할 경우)")
    void create_실패_역없음() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        final Line line = 노선_생성(잠실역, 잠실새내역);

        final SectionRequest sectionRequest =
                new SectionRequest(Long.MAX_VALUE, 잠실새내역.getId(), ORIGINAL_DISTANCE);

        // when
        final ResultActions result = 구간_추가(sectionRequest, line.getId());

        //then
        result.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구간 등록 - 실패(자연수가 아닌 거리를 등록할 경우)")
    void create_실패_거리가자연수아님() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        final Station 강남역 = stationDao.create(Station.create("강남역"));
        final Line line = 노선_생성(잠실역, 잠실새내역);

        final SectionRequest 강남_잠실새내 =
                new SectionRequest(강남역.getId(), 잠실새내역.getId(), 0);

        // when
        final ResultActions result = 구간_추가(강남_잠실새내, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("자연수가 아닌 거리를 등록하셨습니다."));
    }


    @Test
    @DisplayName("구간 등록 - 실패(의미상 중복된 섹션을 등록할 경우)")
    void create_실패_의미상중복구간() throws Exception {
        //given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        final Line line = 노선_생성(잠실역, 잠실새내역);
        final SectionRequest 잠실새내_잠실 = new SectionRequest(잠실새내역.getId(), 잠실역.getId(), 3);

        //when
        ResultActions result = 구간_추가(잠실새내_잠실, line.getId());

        //then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("중복된 구간입니다."));
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로운 섹션의 두 역이 모두 같은 노선에 포함된 경우)")
    void create_실패_사이클도는구간() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        final Station 강남역 = stationDao.create(Station.create("강남역"));
        final Line line = 노선_생성(잠실역, 강남역);

        final SectionRequest 강남_잠실새내 = new SectionRequest(강남역.getId(), 잠실새내역.getId(), 3);
        final SectionRequest 잠실_잠실새내 = new SectionRequest(잠실역.getId(), 잠실새내역.getId(), 3);

        구간_추가(강남_잠실새내, line.getId());

        // when
        final ResultActions result = 구간_추가(잠실_잠실새내, line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("사이클이 생기는 구간입니다."));
    }

    @Test
    @DisplayName("구간 제거 - 성공")
    void delete_성공() throws Exception {
        // given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Line line = 노선_생성(잠실역, 잠실새내역);

        Station 강남역 = stationDao.create(Station.create("강남역"));

        SectionRequest 강남_잠실 =
                new SectionRequest(강남역.getId(), 잠실역.getId(), 4);

        구간_추가(강남_잠실, line.getId());
        // when
        ResultActions result = mockMvc.perform(
                delete("/lines/" + line.getId() + "/sections?stationId=" + 잠실역.getId()));
        final Sections sections = sectionDao.findSectionsByLineId(line.getId());

        // then
        result.andDo(print())
                .andExpect(status().isNoContent());

        assertThat(sections.sections().size()).isEqualTo(1);
        assertThat(sections.sections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(sections.sections().get(0).getDownStation().getName()).isEqualTo("잠실새내역");
        assertThat(sections.sections().get(0).getDistance()).isEqualTo(14);
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선이 존재하지 않을 시)")
    public void delete_실패_노선없음() throws Exception {
        //given & when
        final ResultActions result = mockMvc.perform(
                delete("/lines/" + Long.MAX_VALUE + "/sections?stationId=1"));
        //then

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구간 제거 - 실패(역이 해당 노선에 등록되어 있지 않을 시)")
    public void delete_실패_역이노선에없음() throws Exception {
        //given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Line line = 노선_생성(잠실역, 잠실새내역);

        //when
        final ResultActions result = mockMvc.perform(
                delete("/lines/" + line.getId() + "/sections?stationId=" + Long.MAX_VALUE)
        );

        //then
        result.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선에 구간이 하나밖에 존재하지 않을 시)")
    public void delete_실패_마지막남은구간() throws Exception {
        //given
        final Station 잠실역 = 상행역();
        final Station 잠실새내역 = 하행역();
        Line line = 노선_생성(잠실역, 잠실새내역);

        //when
        final ResultActions result = mockMvc.perform(
                delete("/lines/" + line.getId() + "/sections?stationId=" + 잠실새내역.getId())
        );

        //then
        result.andExpect(status().isBadRequest());
    }

    private ResultActions 구간_추가(SectionRequest sectionRequest, Long lineId) throws Exception {
        return mockMvc.perform(post("/lines/" + lineId + "/sections")
                .content(objectMapper.writeValueAsString(sectionRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    private Line 노선_생성(Station upStation, Station downStation) {
        return lineService.create("1호선", "bg-blue-300", upStation, downStation, ORIGINAL_DISTANCE);
    }

    private Station 하행역() {
        return stationDao.create(Station.create(DOWN_STATION_NAME));
    }

    private Station 상행역() {
        return stationDao.create(Station.create(UP_STATION_NAME));
    }
}
