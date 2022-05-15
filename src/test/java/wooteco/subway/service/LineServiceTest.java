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
import wooteco.subway.dto.LineDto;
import wooteco.subway.dto.StationDto;
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
        StationService stationService = new StationService(stationDao);
        SectionService sectionService = new SectionService(new FakeSectionDao());
        lineService = new LineService(new FakeLineDao(), stationService, sectionService);
        lineService.save("신분당선", "red", 1L, 2L, 3);
    }

    @Test
    @DisplayName("노선을 추가할 수 있다.")
    void createLine_success() {
        LineDto lineDto = lineService.save("2호선", "green", 3L, 4L, 3);
        StationDto upStation = lineDto.getStations().get(0);
        StationDto downStation = lineDto.getStations().get(1);

        assertAll(
                () -> assertThat(lineDto.getId()).isEqualTo(2L),
                () -> assertThat(lineDto.getName()).isEqualTo("2호선"),
                () -> assertThat(lineDto.getColor()).isEqualTo("green"),
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
        assertThatThrownBy(() -> lineService.save(name, color, 1L, 2L, 3))
                .isInstanceOf(DuplicatedLineNameException.class);
    }

    @DisplayName("Id에 해당하는 노선을 반환한다.")
    @Test
    void findLineById() {
        LineDto lineDto = lineService.findLineById(1L);
        StationDto upStation = lineDto.getStations().get(0);
        StationDto downStation = lineDto.getStations().get(1);

        assertAll(
                () -> assertThat(lineDto.getId()).isEqualTo(1L),
                () -> assertThat(lineDto.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineDto.getColor()).isEqualTo("red"),
                () -> assertThat(upStation.getName()).isEqualTo("강남역"),
                () -> assertThat(upStation.getId()).isEqualTo(1L),
                () -> assertThat(downStation.getName()).isEqualTo("판교역"),
                () -> assertThat(downStation.getId()).isEqualTo(2L)
        );
    }

    @Test
    @DisplayName("모든 노선을 반환한다.")
    void findAll() {
        lineService.save("2호선", "green", 3L, 4L, 3);
        final List<LineDto> lines = lineService.findAll();
        LineDto firstLine = lines.get(0);
        LineDto secondLine = lines.get(1);
        List<StationDto> firstLineStations = firstLine.getStations();
        List<StationDto> secondLineStations = secondLine.getStations();

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
        lineService.update(1L, "6호선", "brown");
        LineDto lineDto = lineService.findLineById(1L);
        assertAll(
                () -> assertThat(lineDto.getName()).isEqualTo("6호선"),
                () -> assertThat(lineDto.getColor()).isEqualTo("brown")
        );
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
        assertThatThrownBy(() -> lineService.update(2L, "6호선", "brown"))
                .isInstanceOf(InvalidLineIdException.class);
    }
}
