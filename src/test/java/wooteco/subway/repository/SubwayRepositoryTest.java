package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

@DisplayName("지하철 Repository")
@JdbcTest
class SubwayRepositoryTest {

    private static final Station 강남역 = new Station("강남역");
    private static final Station 역삼역 = new Station("역삼역");
    private static final Station 선릉역 = new Station("선릉역");
    private static final Station 삼성역 = new Station("삼성역");

    @Autowired
    private DataSource dataSource;
    private SubwayRepository subwayRepository;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(dataSource);
        SectionDao sectionDao = new SectionDao(dataSource);
        StationDao stationDao = new StationDao(dataSource);
        this.subwayRepository = new SubwayRepository(lineDao, sectionDao, stationDao);
    }

    @DisplayName("지하철역 Repository")
    @Nested
    class StationRepositoryTest {

        private StationRepository stationRepository;

        @BeforeEach
        void setUp() {
            this.stationRepository = subwayRepository;
        }

        @DisplayName("역을 생성한다.")
        @Test
        void saveStation() {
            Long actual = (stationRepository.saveStation(강남역)).getId();
            assertThat(actual).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 역 이름으로 역을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"강남역"})
        void saveStationWithDuplicatedName(String name) {
            stationRepository.saveStation(new Station(name));

            assertThatThrownBy(() -> stationRepository.saveStation(new Station(name)))
                    .isInstanceOf(DuplicateStationNameException.class)
                    .hasMessageContaining("해당 이름의 지하철역은 이미 존재합니다.");
        }

        @DisplayName("역 목록을 조회한다.")
        @Test
        void findStations() {
            List<Station> expected = List.of(강남역, 역삼역, 선릉역);
            expected.forEach(stationRepository::saveStation);

            List<Station> actual = stationRepository.findStations();
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expected);
        }

        @DisplayName("역을 조회한다.")
        @Test
        void findStationById() {
            Station station = stationRepository.saveStation(강남역);
            Station actual = stationRepository.findStationById(station.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(station.getId()),
                    () -> assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("id")
                            .isEqualTo(강남역));
        }


        @DisplayName("역을 삭제한다.")
        @Test
        void removeStation() {
            Stream.of(강남역)
                    .map(stationRepository::saveStation)
                    .map(Station::getId)
                    .forEach(stationRepository::removeStation);

            List<Station> actual = stationRepository.findStations();
            assertThat(actual).isEmpty();
        }

        @DisplayName("존재하지 않는 역을 삭제한다.")
        @Test
        void removeNonExistentStation() {
            assertThatThrownBy(() -> stationRepository.removeStation(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철역을 찾을 수 없습니다.");
        }
    }

    @DisplayName("지하철노선 Repository")
    @Nested
    class LineRepositoryTest {

        private LineRepository lineRepository;
        private List<Section> sections;

        @BeforeEach
        void setUp() {
            lineRepository = subwayRepository;

            Station station1 = subwayRepository.saveStation(강남역);
            Station station2 = subwayRepository.saveStation(역삼역);
            this.sections = List.of(new Section(station1, station2, 3));
        }

        @DisplayName("지하철노선을 생성한다.")
        @Test
        void saveLine() {
            Line line = new Line(sections, "신분당선", "color");

            Long actual = (lineRepository.saveLine(line)).getId();
            assertThat(actual).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"강남역"})
        void saveWithDuplicatedName(String name) {
            Line firstLine = new Line(sections, name, "color1");
            Line secondLine = new Line(sections, name, "color2");

            lineRepository.saveLine(firstLine);
            assertThatThrownBy(() -> lineRepository.saveLine(secondLine))
                    .isInstanceOf(DuplicateLineNameException.class)
                    .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 저장한다.")
        @ParameterizedTest
        @ValueSource(strings = {"color"})
        void saveWithDuplicatedColor(String color) {
            Line firstLine = new Line(sections, "신분당선", color);
            Line secondLine = new Line(sections, "분당선", color);

            lineRepository.saveLine(firstLine);
            assertThatThrownBy(() -> lineRepository.saveLine(secondLine))
                    .isInstanceOf(DuplicateLineColorException.class)
                    .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("지하철노선 목록을 조회한다.")
        @ParameterizedTest
        @ValueSource(ints = {3})
        void findLines(int expected) {
            IntStream.rangeClosed(1, expected)
                    .mapToObj(id -> new Line(sections, "호선" + id, "색상" + id))
                    .forEach(lineRepository::saveLine);

            List<Line> actual = lineRepository.findLines();
            assertThat(actual).hasSize(expected);
        }

        @DisplayName("지하철노선을 조회한다.")
        @Test
        void findLineById() {
            Line expected = new Line(sections, "신분당선", "color");
            Long lineId = lineRepository.saveLine(expected).getId();

            Line actual = lineRepository.findLineById(lineId);
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("id", "sections")
                    .isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 지하철노선을 조회한다.")
        @Test
        void findNonExistentLine() {
            assertThatThrownBy(() -> lineRepository.findLineById(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }

        @DisplayName("지하철노선을 수정한다.")
        @Test
        void updateLine() {
            Line expected = lineRepository.saveLine(new Line(sections, "신분당선", "color1"));
            expected.update("분당선", "color2");

            Line actual = lineRepository.updateLine(expected);
            assertThat(actual).usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 지하철노선을 수정한다.")
        @Test
        void updateNonExistentLine() {
            assertThatThrownBy(() -> lineRepository.updateLine(new Line(1L, sections, "신분당선", "color")))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }

        @DisplayName("구간 목록을 수정한다.")
        @Test
        void updateSections() {
            Station upStation = subwayRepository.saveStation(new Station("광교역"));
            Station middleStation = subwayRepository.saveStation(new Station("광교중앙역"));
            Station downStation = subwayRepository.saveStation(new Station("상현역"));

            Line line = subwayRepository.saveLine(new Line(
                    List.of(new Section(upStation, downStation, 10)), "신분당선", "red"));

            List<Section> expected = List.of(
                    new Section(upStation, middleStation, 5),
                    new Section(middleStation, downStation, 5));
            lineRepository.updateSections(new Line(line.getId(), expected, line.getName(), line.getColor()));

            List<Section> actual = lineRepository.findLineById(line.getId()).getSections();
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expected);
        }

        @DisplayName("지하철노선을 삭제한다.")
        @Test
        void removeLine() {
            Stream.of(new Line(sections, "신분당선", "color"))
                    .map(lineRepository::saveLine)
                    .map(Line::getId)
                    .forEach(lineRepository::removeLine);

            List<Line> actual = lineRepository.findLines();
            assertThat(actual).isEmpty();
        }

        @DisplayName("존재하지 않는 지하철노선을 삭제한다.")
        @Test
        void removeNonExistentLine() {
            assertThatThrownBy(() -> lineRepository.removeLine(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("지하철노선을 찾을 수 없습니다.");
        }
    }
}
