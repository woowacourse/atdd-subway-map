package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.dao.FakeLineDao;
import wooteco.subway.dao.FakeSectionDao;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.line.DuplicatedLineException;
import wooteco.subway.exception.line.LineNotFoundException;

class LineServiceTest {

    private LineService lineService;
    private SectionService sectionService;
    private StationService stationService;

    private Line line;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(new FakeSectionDao());
        stationService = new StationService(new FakeStationDao());
        lineService = new LineService(new FakeLineDao(), sectionService, stationService);

        Station station1 = stationService.save(new Station("선릉역"));
        Station station2 = stationService.save(new Station("강남역"));

        Section section = new Section(station1.getId(), station2.getId(), 5);
        line = lineService.save(new Line("신분당선", "red"), section);
    }

    @DisplayName("추가하려는 노선의 이름 혹은 색이 이미 존재하면 예외를 발생시킨다.")
    @ParameterizedTest
    @CsvSource({"2호선, red", "신분당선, blue", "신분당선, red"})
    void createLine_exception(String name, String color) {
        Station station1 = stationService.save(new Station("잠실역"));
        Station station2 = stationService.save(new Station("잠실새내역"));
        Section newSection = new Section(station1.getId(), station2.getId(), 5);

        assertThatThrownBy(() -> lineService.save(new Line(name, color), newSection))
                .isInstanceOf(DuplicatedLineException.class);
    }

    @DisplayName("새로운 노선을 추가할 수 있다.")
    @Test
    void findLineById() {
        Line actual = lineService.findLineById(line.getId());

        assertThat(actual).isEqualTo(new Line(1L, "신분당선", "red"));
    }

    @DisplayName("노선을 삭제할 수 있다.")
    @Test
    void deleteLine_success() {
        lineService.deleteById(line.getId());

        assertThat(lineService.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 노선을 삭제하려하면 예외를 발생시킨다.")
    @Test
    void deleteLine_exception() {
        assertThatThrownBy(() -> lineService.deleteById(2L))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 반환하려하면 예외를 발생시킨다.")
    @Test
    void findLineById_exception() {
        assertThatThrownBy(() -> lineService.findLineById(-1L))
                .isInstanceOf(LineNotFoundException.class);
    }

    @DisplayName("존재하지 않는 노선을 수정하려하면 예외를 발생시킨다.")
    @Test
    void updateLineById_exception() {
        assertThatThrownBy(() -> lineService.update(new Line(2L, "6호선", "brown")))
                .isInstanceOf(LineNotFoundException.class);
    }
}
