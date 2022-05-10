package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.line.DuplicatedLineNameException;
import wooteco.subway.exception.line.InvalidLineIdException;

class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        StationDao stationDao = new FakeStationDao();
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("판교역"));
        stationDao.save(new Station("서울대입구역"));
        stationDao.save(new Station("사당역"));
        lineService = new LineService(new FakeLineDao(), stationDao, new FakeSectionDao());
        lineService.save(new LineRequest("신분당선", "red", 1L, 2L, 3));
    }

    @Test
    @DisplayName("노선을 추가할 수 있다.")
    void createLine_success() {
        final LineResponse lineResponse = lineService.save(
                new LineRequest("2호선", "green", 3L, 4L, 3));
        StationResponse upStation = lineResponse.getStations().get(0);
        StationResponse downStation = lineResponse.getStations().get(1);

        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(2L),
                () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("green"),
                () -> assertThat(upStation.getName()).isEqualTo("서울대입구역"),
                () -> assertThat(upStation.getId()).isEqualTo(3L),
                () -> assertThat(downStation.getName()).isEqualTo("사당역"),
                () -> assertThat(downStation.getId()).isEqualTo(4L)
        );
    }

    @DisplayName("추가하려는 노선의 이름 혹은 색이 이미 존재하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2호선, red", "신분당선, blue", "신분당선, red"})
    void createLine_exception(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color, 1L, 2L, 3);
        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(DuplicatedLineNameException.class);
    }

    @DisplayName("Id에 해당하는 노선을 반환한다.")
    @Test
    void findLineById() {
        LineResponse lineResponse = lineService.findLineById(1L);
        StationResponse upStation = lineResponse.getStations().get(0);
        StationResponse downStation = lineResponse.getStations().get(1);

        assertAll(
                () -> assertThat(lineResponse.getId()).isEqualTo(1L),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(upStation.getName()).isEqualTo("강남역"),
                () -> assertThat(upStation.getId()).isEqualTo(1L),
                () -> assertThat(downStation.getName()).isEqualTo("판교역"),
                () -> assertThat(downStation.getId()).isEqualTo(2L)
        );
    }

    @Test
    @DisplayName("모든 노선을 반환한다.")
    void findAll() {
        lineService.save(new LineRequest("2호선", "green", 3L, 4L, 3));
        final List<LineResponse> lines = lineService.findAll();
        LineResponse firstLine = lines.get(0);
        LineResponse secondLine = lines.get(1);
        List<StationResponse> firstLineStations = firstLine.getStations();
        List<StationResponse> secondLineStations = secondLine.getStations();

        assertAll(
                () -> assertThat(firstLine.getId()).isEqualTo(1L),
                () -> assertThat(firstLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(firstLine.getColor()).isEqualTo("red"),
                () -> assertThat(secondLine.getId()).isEqualTo(2L),
                () -> assertThat(secondLine.getName()).isEqualTo("2호선"),
                () -> assertThat(secondLine.getColor()).isEqualTo("green"),
                () -> assertThat(firstLineStations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(firstLineStations.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(firstLineStations.get(1).getId()).isEqualTo(2L),
                () -> assertThat(firstLineStations.get(1).getName()).isEqualTo("판교역"),
                () -> assertThat(secondLineStations.get(0).getId()).isEqualTo(3L),
                () -> assertThat(secondLineStations.get(0).getName()).isEqualTo("서울대입구역"),
                () -> assertThat(secondLineStations.get(1).getId()).isEqualTo(4L),
                () -> assertThat(secondLineStations.get(1).getName()).isEqualTo("사당역")
        );
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void deleteLine_success() {
        lineService.deleteById(1L);

        assertThat(lineService.findAll()).isEmpty();
    }

    @Test
    @DisplayName("노선 정보를 수정할 수 있다.")
    void update_success() {
        LineRequest lineRequest = new LineRequest("6호선", "brown", null, null, 0);
        lineService.update(1L, lineRequest);
        lineService.findLineById(1L);
    }

    @DisplayName("존재하지 않는 노선을 삭제하려하면 예외를 발생시킨다.")
    @Test
    void deleteLine_exception() {
        assertThatThrownBy(() -> lineService.deleteById(2L))
                .isInstanceOf(InvalidLineIdException.class);
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_exception() {
        assertThatThrownBy(() -> lineService.findLineById(2L))
                .isInstanceOf(InvalidLineIdException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정하려하면 예외를 발생시킨다.")
    @Test
    void updateLineById_exception() {
        LineRequest lineRequest = new LineRequest("6호선", "brown", null, null, 0);
        assertThatThrownBy(() -> lineService.update(2L, lineRequest))
                .isInstanceOf(InvalidLineIdException.class);
    }
}
