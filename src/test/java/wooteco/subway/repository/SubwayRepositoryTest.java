package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.repository.exception.DuplicateStationNameException;

@JdbcTest
class SubwayRepositoryTest {

    @Autowired
    private DataSource dataSource;
    private wooteco.subway.repository.dao.LineDao lineDao;
    private wooteco.subway.repository.dao.SectionDao sectionDao;
    private wooteco.subway.repository.dao.StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new LineDao(dataSource);
        this.sectionDao = new SectionDao(dataSource);
        this.stationDao = new StationDao(dataSource);
    }

    @Nested
    class StationRepositoryTest {

        private StationRepository stationRepository;

        @BeforeEach
        void setUp() {
            this.stationRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
        }

        @DisplayName("지하철역을 생성한다.")
        @Test
        void save() {
            Station station = stationRepository.saveStation(new Station("강남역"));
            assertThat(station.getId()).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"강남역"})
        void saveWithExistentName(String name) {
            stationRepository.saveStation(new Station(name));
            assertThatThrownBy(() -> stationRepository.saveStation(new Station(name)))
                    .isInstanceOf(DuplicateStationNameException.class)
                    .hasMessageContaining("해당 이름의 지하철역은 이미 존재합니다.");
        }

        @DisplayName("지하철역 목록을 조회한다.")
        @Test
        void findAll() {
            List<Station> stations = List.of(
                    new Station("강남역"),
                    new Station("역삼역"),
                    new Station("선릉역")
            );
            stations.forEach(stationRepository::saveStation);
            assertThat(stationRepository.findStations()).hasSize(3);
        }

        @DisplayName("지하철역을 삭제한다.")
        @Test
        void remove() {
            Station station = stationRepository.saveStation(new Station("강남역"));
            stationRepository.removeStation(station.getId());
            assertThat(stationRepository.findStations()).isEmpty();
        }

        @DisplayName("존재하지 않는 지하철역을 삭제한다.")
        @Test
        void removeWithNonexistentId() {
            assertThatThrownBy(() -> stationRepository.removeStation(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철역을 찾을 수 없습니다.");
        }
    }

    @Nested
    class LineRepositoryTest {

        private LineRepository lineRepository;
        private List<Section> sections;

        @BeforeEach
        void setUp() {
            lineRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
            initData();
        }

        private void initData() {
            StationRepository stationRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
            Station station1 = stationRepository.saveStation(new Station("강남역"));
            Station station2 = stationRepository.saveStation(new Station("선릉역"));
            sections = List.of(new Section(station1, station2, 3));
        }

        @DisplayName("지하철노선을 생성한다.")
        @Test
        void save() {
            Line line = lineRepository.saveLine(new Line(sections, "신분당선", "color"));
            assertThat(line.getId()).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"강남역"})
        void saveWithExistentName(String name) {
            lineRepository.saveLine(new Line(sections, name, "color1"));
            assertThatThrownBy(() -> lineRepository.saveLine(new Line(sections, name, "color2")))
                    .isInstanceOf(DuplicateLineNameException.class)
                    .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"color"})
        void saveWithExistentColor(String color) {
            lineRepository.saveLine(new Line(sections, "신분당선", color));
            assertThatThrownBy(() -> lineRepository.saveLine(new Line(sections, "분당선", color)))
                    .isInstanceOf(DuplicateLineColorException.class)
                    .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("지하철노선 목록을 조회한다.")
        @Test
        void findAll() {
            List<Line> lines = List.of(
                    new Line(sections, "신분당선", "color1"),
                    new Line(sections, "분당선", "color2"));
            lines.forEach(lineRepository::saveLine);
            assertThat(lineRepository.findLines()).hasSize(2);
        }

        @DisplayName("지하철노선을 조회한다.")
        @Test
        void findById() {
            Line expected = new Line(sections, "신분당선", "color");
            Line actual = lineRepository.saveLine(expected);
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("id", "sections")
                    .isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 지하철노선을 조회한다.")
        @Test
        void findWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.findLineById(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }

        @DisplayName("지하철노선을 수정한다.")
        @Test
        void update() {
            Line line = lineRepository.saveLine(new Line(sections, "신분당선", "color1"));
            line.update("분당선", "color2");

            Line updatedLine = lineRepository.updateLine(line);

            assertAll(() -> {
                assertThat(updatedLine.getName()).isEqualTo("분당선");
                assertThat(updatedLine.getColor()).isEqualTo("color2");
            });
        }

        @DisplayName("존재하지 않는 지하철노선을 수정한다.")
        @Test
        void updateWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.updateLine(new Line(1L, sections, "신분당선", "color")))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }

        @DisplayName("지하철노선을 삭제한다.")
        @Test
        void remove() {
            Line line = lineRepository.saveLine(new Line(sections, "신분당선", "color"));
            lineRepository.removeLine(line.getId());
            assertThat(lineRepository.findLines()).isEmpty();
        }

        @DisplayName("존재하지 않는 지하철노선을 삭제한다.")
        @Test
        void removeWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.removeLine(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }
    }
}
