package wooteco.subway.line.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
import wooteco.subway.ApiControllerTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.dao.StationDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("노선 관련 테스트")
class LineApiControllerTest extends ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionService sectionService;

    @Test
    @DisplayName("노선 생성 - 성공")
    void createLine_success() throws Exception {
        // given
        Long 잠실역_id = stationDao.save(Station.from("잠실역")).getId();
        Long 잠실새내_id = stationDao.save(Station.from("잠실새내역")).getId();

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, 10);

        // when
        ResultActions result = 노선_생성(이호선);

        // then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("2호선"))
                .andExpect(jsonPath("color").value("bg-green-600"))
                .andExpect(jsonPath("stations").isArray())
                .andExpect(jsonPath("stations[0].name").value("잠실역"))
                .andExpect(jsonPath("stations[1].name").value("잠실새내역"));
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 이름)")
    void createLine_duplicatedName() throws Exception {
        // given
        lineDao.save(Line.create("1호선", "bg-red-600"));

        Long 잠실역_id = stationDao.save(Station.from("잠실역")).getId();
        Long 석촌역_id = stationDao.save(Station.from("석촌역")).getId();

        final LineRequest 일호선 =
                new LineRequest("1호선", "bg-green-600", 잠실역_id, 석촌역_id, 10);

        // when
        ResultActions result = 노선_생성(일호선);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("중복되는 라인 정보가 존재합니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 컬러)")
    void createLine_duplicatedColor() throws Exception {
        // given
        lineDao.save(Line.create("1호선", "bg-green-600"));

        Long 잠실역_id = stationDao.save(Station.from("잠실역")).getId();
        Long 석촌역_id = stationDao.save(Station.from("석촌역")).getId();

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 석촌역_id, 10);

        // when
        ResultActions result = 노선_생성(이호선);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("중복되는 라인 정보가 존재합니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(request 필수값 누락)")
    void createLine_notSatisfiedRequest() throws Exception {
        // given
        LineRequest 이름없는노선 =
                new LineRequest("", "bg-green-600", 1L, 2L, 10);

        // when
        ResultActions result = 노선_생성(이름없는노선);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("필수값이 잘못 되었습니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(등록되지 않는 역을 노선 종점역에 등록할 때)")
    void createLine_notExistStation() throws Exception {
        // given
        LineRequest 삼호선 =
                new LineRequest("3호선", "bg-green-600", 1L, 2L, 10);

        // when
        ResultActions result = 노선_생성(삼호선);

        // then
        result.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("노선 생성 - 실패(상행선과 하행선 역이 같을 경우)")
    void createLine_sameStations() throws Exception {
        // given
        Long 잠실역_id = stationDao.save(Station.from("잠실역")).getId();

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실역_id, 10);

        // when
        ResultActions result = 노선_생성(이호선);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("필수값이 잘못 되었습니다."));
    }

    @DisplayName("노선 조회 - 성공")
    @Test
    void readLine_success() throws Exception {
        //given
        Station 강남역 = stationDao.save(Station.from("강남역"));
        Station 잠실역 = stationDao.save(Station.from("잠실역"));
        Station 석촌역 = stationDao.save(Station.from("석촌역"));
        LineRequest 사호선 =
                new LineRequest("4호선", "bg-blue-600", 강남역.getId(), 잠실역.getId(), 10);
        ResultActions createdLineResult = 노선_생성(사호선);
        LineResponse lineResponse =
                objectMapper.readValue(createdLineResult.andReturn().getResponse().getContentAsString(), LineResponse.class);
        sectionService.createSection(Section.create(강남역, 석촌역, 5), lineResponse.getId());

        //when
        ResultActions result = mockMvc.perform(get("/lines/" + lineResponse.getId()));

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("stations[*].name", Matchers.containsInRelativeOrder("강남역", "석촌역", "잠실역")));
    }

    @Test
    @DisplayName("노선 조회 - 실패(해당 노선이 없을 경우)")
    void readLine_fail_notExistLine() throws Exception {
        // given & when
        ResultActions result = mockMvc.perform(get("/lines/" + Long.MAX_VALUE));

        //then
        result.andDo(print())
                .andExpect(status().isNotFound());
    }

    private ResultActions 노선_생성(LineRequest lineRequest) throws Exception {
        return mockMvc.perform(post("/lines")
                .content(objectMapper.writeValueAsString(lineRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }
}
