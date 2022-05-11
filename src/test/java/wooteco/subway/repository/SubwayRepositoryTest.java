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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.dao.jdbc.JdbcLineDao;
import wooteco.subway.repository.dao.jdbc.JdbcStationDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.repository.exception.DuplicateStationNameException;

@JdbcTest
class SubwayRepositoryTest {

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new JdbcLineDao(dataSource);
        this.stationDao = new JdbcStationDao(dataSource);
    }

    @Nested
    class LineRepositoryTest {

        private LineRepository lineRepository;

        @BeforeEach
        void setUp() {
            lineRepository = new SubwayRepository(lineDao, stationDao);
        }

        @DisplayName("지하철노선을 생성한다.")
        @Test
        void save() {
            Line line = new Line("신분당선", "color");
            Long lineId = lineRepository.saveLine(line);
            assertThat(lineId).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 저장한다.")
        @Test
        void saveWithExistentName() {
            String name = "강남역";
            lineRepository.saveLine(new Line(name, "color1"));
            assertThatThrownBy(() -> lineRepository.saveLine(new Line(name, "color2")))
                    .isInstanceOf(DuplicateLineNameException.class)
                    .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 저장한다.")
        @Test
        void saveWithExistentColor() {
            String color = "color";
            lineRepository.saveLine(new Line("신분당선", color));
            assertThatThrownBy(() -> lineRepository.saveLine(new Line("분당선", color)))
                    .isInstanceOf(DuplicateLineColorException.class)
                    .hasMessageContaining("해당 색상의 지하철노선은 이미 존재합니다.");
        }

        @DisplayName("지하철노선 목록을 조회한다.")
        @Test
        void findAll() {
            List<Line> lines = List.of(
                    new Line("신분당선", "color1"),
                    new Line("분당선", "color2")
            );
            lines.forEach(lineRepository::saveLine);
            assertThat(lineRepository.findLines()).hasSize(2);
        }

        @DisplayName("지하철노선을 조회한다.")
        @Test
        void findById() {
            String name = "신분당선";
            String color = "color";
            Long lineId = lineRepository.saveLine(new Line(name, color));
            Line line = lineRepository.findLineById(lineId);
            assertAll(() -> {
                assertThat(line.getName()).isEqualTo(name);
                assertThat(line.getColor()).isEqualTo(color);
            });
        }

        @DisplayName("존재하지 않는 지하철노선을 조회한다.")
        @Test
        void findWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.findLineById(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("조회하고자 하는 지하철노선이 존재하지 않습니다.");
        }

        @DisplayName("지하철노선을 수정한다.")
        @Test
        void update() {
            Long lineId = lineRepository.saveLine(new Line("신분당선", "color1"));
            Line line = lineRepository.findLineById(lineId);
            line.update("분당선", "color2");

            lineRepository.updateLine(line);
            Line updatedLine = lineRepository.findLineById(lineId);

            assertAll(() -> {
                assertThat(updatedLine.getName()).isEqualTo("분당선");
                assertThat(updatedLine.getColor()).isEqualTo("color2");
            });
        }

        @DisplayName("존재하지 않는 지하철노선을 수정한다.")
        @Test
        void updateWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.updateLine(new Line(1L, "신분당선", "color")))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("수정하고자 하는 지하철노선이 존재하지 않습니다.");
        }

        @DisplayName("지하철노선을 삭제한다.")
        @Test
        void remove() {
            Long lineId = lineRepository.saveLine(new Line("신분당선", "color"));
            lineRepository.removeLine(lineId);
            assertThat(lineRepository.findLines()).isEmpty();
        }

        @DisplayName("존재하지 않는 지하철노선을 삭제한다.")
        @Test
        void removeWithNonexistentId() {
            assertThatThrownBy(() -> lineRepository.removeLine(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("삭제하고자 하는 지하철노선이 존재하지 않습니다.");
        }
    }

    @Nested
    class StationRepositoryTest {

        private StationRepository stationRepository;

        @BeforeEach
        void setUp() {
            this.stationRepository = new SubwayRepository(lineDao, stationDao);
        }

        @DisplayName("지하철역을 생성한다.")
        @Test
        void save() {
            Station station = new Station("강남역");
            Long stationId = stationRepository.saveStation(station);
            assertThat(stationId).isGreaterThan(0L);
        }

        @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 저장한다.")
        @Test
        void saveWithExistentName() {
            String name = "강남역";
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
            Long stationId = stationRepository.saveStation(new Station("강남역"));
            stationRepository.removeStation(stationId);
            assertThat(stationRepository.findStations()).isEmpty();
        }

        @DisplayName("존재하지 않는 지하철역을 삭제한다.")
        @Test
        void removeWithNonexistentId() {
            assertThatThrownBy(() -> stationRepository.removeStation(1L))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("삭제하고자 하는 지하철역이 존재하지 않습니다.");
        }
    }
}
