package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class LineServiceTest {

    private LineService lineService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        LineDao lineDao = new LineDao(jdbcTemplate);
        SectionDao sectionDao = new SectionDao(jdbcTemplate);
        lineService = new LineService(stationDao, lineDao, sectionDao);

        stationDao.save("강남역");
        stationDao.save("선릉역");
        stationDao.save("잠실역");
        stationDao.save("교대역");
        stationDao.save("대치역");
    }


    @Test
    @DisplayName("중복된 이름을 저장한다.")
    void duplicatedNameException() {
        //given
        String 신분당선 = "신분당선";
        LineRequest lineRequest = new LineRequest(신분당선, "red", 1L, 2L, 1);
        //when
        lineService.saveLine(lineRequest);
        //then
        assertThatThrownBy(() -> lineService.saveLine(new LineRequest(신분당선, "blue", 1L, 2L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @Test
    @DisplayName("중복된 색을 저장한다.")
    void duplicatedColorException() {
        //given
        String color = "orange";
        LineRequest lineRequest = new LineRequest("2호선", color, 1L, 2L, 1);
        //when
        lineService.saveLine(lineRequest);
        //then
        assertThatThrownBy(() -> lineService.saveLine(new LineRequest("3호선", color, 1L, 2L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @DisplayName("삭제할 line 의 sections 크기가 1일 경우 예외를 발생시킨다.")
    @Test
    void SectionSizeException() {
        //given

        //when
        lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 5));
        //then
        assertThatThrownBy(() -> lineService.deleteSectionByLineIdAndStationId(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 구간이 1개 이상은 존재해야합니다.");
    }

    @DisplayName("종점인 station id 와 line id 를 이용하여 section 을 삭제한다.")
    @Test
    void deleteSectionByFinalStationIdAndLineId() {
        //given
        lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 5));
        lineService.saveSection(1L, new SectionRequest(2L, 3L, 5));
        lineService.saveSection(1L, new SectionRequest(3L, 4L, 5));
        lineService.saveSection(1L, new SectionRequest(4L, 5L, 5));
        //when
        lineService.deleteSectionByLineIdAndStationId(1L, 1L);
        //then
        List<LineResponse> lines = lineService.findLineAll();
        List<Long> lineIds = lines.get(0).getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(lineIds).isEqualTo(List.of(2L, 3L, 4L, 5L));
    }

    @DisplayName("종점 전역인 station id 와 line id 를 이용하여 section 을 삭제한다.")
    @Test
    void deleteSectionBySemeFinalStationIdAndLineId() {
        //given
        lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 5));
        lineService.saveSection(1L, new SectionRequest(2L, 3L, 5));
        lineService.saveSection(1L, new SectionRequest(3L, 4L, 5));
        lineService.saveSection(1L, new SectionRequest(4L, 5L, 5));
        //when
        lineService.deleteSectionByLineIdAndStationId(1L, 2L);
        //then
        List<LineResponse> lines = lineService.findLineAll();
        List<Long> lineIds = lines.get(0).getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(lineIds).isEqualTo(List.of(1L, 3L, 4L, 5L));
    }

    @DisplayName("중간 역인 station id 와 line id 를 이용하여 section 을 삭제한다.")
    @Test
    void deleteSectionByMidStationIdAndLineId() {
        //given
        lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 5));
        lineService.saveSection(1L, new SectionRequest(2L, 3L, 5));
        lineService.saveSection(1L, new SectionRequest(3L, 4L, 5));
        lineService.saveSection(1L, new SectionRequest(4L, 5L, 5));
        //when
        lineService.deleteSectionByLineIdAndStationId(1L, 3L);
        //then
        List<LineResponse> lines = lineService.findLineAll();
        List<Long> lineIds = lines.get(0).getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(lineIds).isEqualTo(List.of(1L, 2L, 4L, 5L));
    }

}