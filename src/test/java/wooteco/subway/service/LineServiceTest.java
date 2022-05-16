package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class LineServiceTest {

    private LineService lineService;
    private SectionDao sectionDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        StationDao stationDao = new StationDao(jdbcTemplate);
        LineDao lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
        lineService = new LineService(stationDao, lineDao, sectionDao);

        stationDao.save("강남역");
        stationDao.save("선릉역");
        stationDao.save("잠실역");
        stationDao.save("교대역");
        stationDao.save("대치역");
    }

    @DisplayName("line 을 저장할 때 section 도 저장한다.")
    @Test
    void save() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 2L, 1);
        //when
        LineResponse lineResponse = lineService.saveLine(lineRequest);
        //then
        Line line = new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
        List<Station> stations = lineResponse.getStations().stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());

        assertAll(() -> assertThat(line).isEqualTo(new Line(1L, "신분당선", "red")),
                () -> assertThat(stations).isEqualTo(List.of(new Station(1L, "강남역"), new Station(2L, "선릉역"))));
    }

    @DisplayName("line 을 저장할 때 lineRequest 의 상행역이 존재하지 않는 경우 예외를 발생시킨다.")
    @Test
    void saveExceptionNotFoundUpStation() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 6L, 1L, 1);
        //when

        //then
        assertThatThrownBy(() -> lineService.saveLine(lineRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("상행 역이 존재하지 않습니다.");
    }

    @DisplayName("line 을 저장할 때 lineRequest 의 하행역이 존재하지 않는 경우 예외를 발생시킨다.")
    @Test
    void saveExceptionNotFoundDownStation() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 6L, 1);
        //when

        //then
        assertThatThrownBy(() -> lineService.saveLine(lineRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("하행 역이 존재하지 않습니다.");
    }

    @DisplayName("중복된 이름을 저장한다.")
    @Test
    void saveLineDuplicatedNameException() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 2L, 1);
        //when
        lineService.saveLine(lineRequest);
        //then
        assertThatThrownBy(() -> lineService.saveLine(new LineRequest("신분당선", "blue", 1L, 2L, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 Line 이 존재합니다.");
    }

    @DisplayName("중복된 색을 저장한다.")
    @Test
    void saveLineDuplicatedColorException() {
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

    @DisplayName("저장할 section 이 중간 역이 아닐 때 section 을 저장하고, sections 와 stationIds 의 순서를 확인한다.")
    @Test
    void saveFinalSection() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 2L, 1);
        lineService.saveLine(lineRequest);
        //when
        lineService.saveSection(1L, new SectionRequest(2L, 3L, 5));
        //then
        List<Section> sections = sectionDao.findByLineId(1L);
        List<Station> stations = lineService.findLineById(1L).getStations().stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(sections).isEqualTo(List.of(new Section(1L, 1L, 1L, 2L, 1),
                        new Section(2L, 1L, 2L, 3L, 5))),
                () -> assertThat(stations).isEqualTo(
                        List.of(new Station(1L, "강남역"), new Station(2L, "선릉역"), new Station(3L, "잠실역"))));
    }

    @DisplayName("저장할 section 이 선행 중간 역일 때 section 을 저장하고, sections 와 stationIds 의 순서를 확인한다.")
    @Test
    void saveUpMidSection() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 3L, 10);
        lineService.saveLine(lineRequest);
        //when
        lineService.saveSection(1L, new SectionRequest(1L, 2L, 5));
        //then
        List<Section> sections = sectionDao.findByLineId(1L);
        List<Station> stations = lineService.findLineById(1L).getStations().stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(sections).isEqualTo(List.of(new Section(2L, 1L, 1L, 2L, 5),
                        new Section(3L, 1L, 2L, 3L, 5))),
                () -> assertThat(stations).isEqualTo(
                        List.of(new Station(1L, "강남역"), new Station(2L, "선릉역"), new Station(3L, "잠실역"))));
    }

    @DisplayName("저장할 section 이 하행 중간 역일 때 section 을 저장하고, sections 와 stationIds 의 순서를 확인한다.")
    @Test
    void saveDownMidSection() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 3L, 10);
        lineService.saveLine(lineRequest);
        //when
        lineService.saveSection(1L, new SectionRequest(2L, 3L, 5));
        //then
        List<Section> sections = sectionDao.findByLineId(1L);
        List<Station> stations = lineService.findLineById(1L).getStations().stream()
                .map(it -> new Station(it.getId(), it.getName()))
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(sections).isEqualTo(List.of(new Section(2L, 1L, 1L, 2L, 5),
                        new Section(3L, 1L, 2L, 3L, 5))),
                () -> assertThat(stations).isEqualTo(
                        List.of(new Station(1L, "강남역"), new Station(2L, "선릉역"), new Station(3L, "잠실역"))));
    }

    @DisplayName("저장할 section 이 중간 역일 때 section 을 저장할 때, 저장할 section 의 distance 가 이미 저장되어 있는 section 보다 클 경우 예외를 발생시킨다.")
    @Test
    void saveMidSectionExceptionDistance() {
        //given
        LineRequest lineRequest = new LineRequest("신분당선", "red", 1L, 3L, 1);
        lineService.saveLine(lineRequest);
        //when

        //then
        assertThatThrownBy(() -> lineService.saveSection(1L, new SectionRequest(1L, 2L, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가될 구간의 길이가 기존 구간의 길이보다 깁니다.");
    }

    @DisplayName("조회할 line 의 id가 없는 경우 예외를 발생시킨다.")
    @Test
    void findLineByNotFoundIdException() {
        //given

        //when

        //then
        assertThatThrownBy(() -> lineService.findLineById(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 ID를 가진 노선이 존재하지 않습니다.");
    }

    @DisplayName("lines 를 조회한다.")
    @Test
    void findLines() {
        //given
        lineService.saveLine(new LineRequest("신분당선", "red", 1L, 2L, 10));
        lineService.saveLine(new LineRequest("2호선", "green", 2L, 3L, 10));
        //when
        List<LineResponse> lineResponses = lineService.findLines();
        //then
        List<Line> lines = lineResponses.stream()
                .map(lineResponse -> new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor()))
                .collect(Collectors.toList());
        List<List<Station>> linesStations = lineResponses.stream()
                .map(lineResponse -> lineResponse.getStations().stream()
                        .map(it -> new Station(it.getId(), it.getName()))
                        .collect(Collectors.toList())).collect(Collectors.toList());

        assertAll(() -> assertThat(lines).isEqualTo(List.of(new Line(1L, "신분당선", "red"), new Line(2L, "2호선", "green"))),
                () -> assertThat(linesStations).isEqualTo(
                        List.of(List.of(new Station(1L, "강남역"), new Station(2L, "선릉역")),
                                List.of(new Station(2L, "선릉역"), new Station(3L, "잠실역")))));
    }

    @DisplayName("line 을 수정한다.")
    @Test
    void updateLine() {
        //given
        lineService.saveLine(new LineRequest("신분당선", "red", 1L, 2L, 10));
        //when
        lineService.updateLine(1L, new LineRequest("분당선", "yellow", 1L, 2L, 10));
        //then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor())).isEqualTo(
                new Line(1L, "분당선", "yellow"));
    }

    @DisplayName("line을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        lineService.saveLine(new LineRequest("신분당선", "red", 1L, 2L, 10));
        //when
        lineService.deleteLine(1L);
        //then
        assertThatThrownBy(() -> lineService.findLineById(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 ID를 가진 노선이 존재하지 않습니다.");
    }

    @DisplayName("삭제할 line 의 sections 크기가 1일 경우 예외를 발생시킨다.")
    @Test
    void SectionSizeException() {
        //given

        //when
        lineService.saveLine(new LineRequest("2호선", "green", 1L, 2L, 5));
        //then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L))
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
        lineService.deleteSection(1L, 1L);
        //then
        List<LineResponse> lines = lineService.findLines();
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
        lineService.deleteSection(1L, 2L);
        //then
        List<LineResponse> lines = lineService.findLines();
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
        lineService.deleteSection(1L, 3L);
        //then
        List<LineResponse> lines = lineService.findLines();
        List<Long> lineIds = lines.get(0).getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(lineIds).isEqualTo(List.of(1L, 2L, 4L, 5L));
    }

}