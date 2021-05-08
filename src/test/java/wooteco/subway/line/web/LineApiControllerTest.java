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
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.line.LineDao;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.StationDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LineApiControllerTest {
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
        Long upStationId = stationDao.save(Station.from("잠실역")).getId();
        Long downStationId = stationDao.save(Station.from("잠실새내역")).getId();

        final LineRequest lineRequest =
                new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

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
        lineDao.save(Line.of("1호선", "bg-red-600"));

        Long upStationId = stationDao.save(Station.from("잠실역")).getId();
        Long downStationId = stationDao.save(Station.from("석촌역")).getId();

        final LineRequest lineRequest =
                new LineRequest("1호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("중복되는 라인 정보가 존재합니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 컬러)")
    void createLine_duplicatedColor() throws Exception {
        // given
        lineDao.save(Line.of("1호선", "bg-green-600"));

        Long upStationId = stationDao.save(Station.from("잠실역")).getId();
        Long downStationId = stationDao.save(Station.from("석촌역")).getId();

        final LineRequest lineRequest =
                new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("중복되는 라인 정보가 존재합니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(request 필수값 누락)")
    void createLine_notSatisfiedRequest() throws Exception {
        // given
        LineRequest lineRequest =
                new LineRequest("", "bg-green-600", 1L, 2L, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("필수값이 잘못 되었습니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(등록되지 않는 역을 노선 종점역에 등록할 때)")
    void createLine_notExistStation() throws Exception {
        // given
        LineRequest lineRequest =
                new LineRequest("3호선", "bg-green-600", 1L, 2L, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

        // then선
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("해당 역이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("노선 생성 - 실패(상행선과 하행선 역이 같을 경우)")
    void createLine_sameStations() throws Exception {
        // given
        Long stationId = stationDao.save(Station.from("잠실역")).getId();

        final LineRequest lineRequest =
                new LineRequest("2호선", "bg-green-600", stationId, stationId, 10);

        // when
        ResultActions result = 노선_생성(lineRequest);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("필수값이 잘못 되었습니다."));
    }

    @DisplayName("노선 조회 - 성공")
    @Test
    void readLine_success() throws Exception {
        //given
        Station station1 = stationDao.save(Station.from("강남역"));
        Station station2 = stationDao.save(Station.from("잠실역"));
        Station station3 = stationDao.save(Station.from("석촌역"));
        LineRequest lineRequest =
                new LineRequest("4호선", "bg-blue-600", station1.getId(), station2.getId(), 10);
        ResultActions createdLineResult = 노선_생성(lineRequest);
        LineResponse lineResponse =
                objectMapper.readValue(createdLineResult.andReturn().getResponse().getContentAsString(), LineResponse.class);
        sectionService.createSection(Section.of(station1, station3, 5), lineResponse.getId());

        //when
        ResultActions result = mockMvc.perform(get("/lines/" + lineResponse.getId()));

        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("stations[*].name", Matchers.containsInRelativeOrder("강남역", "석촌역", "잠실역")));
    }

    private ResultActions 노선_생성(LineRequest lineRequest) throws Exception {
        return mockMvc.perform(post("/lines")
                .content(objectMapper.writeValueAsString(lineRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

    private ResultActions 노선_생성(String lineName, String color, String upStationName, String downStationName, int distance) throws Exception {

        Long upStationId = stationDao.save(Station.from(upStationName)).getId();
        Long downStationId = stationDao.save(Station.from(downStationName)).getId();
        final LineRequest lineRequest =
                new LineRequest(lineName, color, upStationId, downStationId, distance);
        return mockMvc.perform(post("/lines")
                .content(objectMapper.writeValueAsString(lineRequest))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }

}
