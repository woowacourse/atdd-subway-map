package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.FakeLineDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.repository.exception.DuplicateStationNameException;

class LineRepositoryTest {

    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        this.lineRepository = new LineRepository(new FakeLineDao());
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void save() {
        Line line = new Line("신분당선", "color");
        Long lineId = lineRepository.save(line);
        assertThat(lineId).isGreaterThan(0L);
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 저장한다.")
    @Test
    void saveWithExistentName() {
        String name = "강남역";
        lineRepository.save(new Line(name, "color1"));
        assertThatThrownBy(() -> lineRepository.save(new Line(name, "color2")))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessageContaining("해당 이름의 지하철노선은 이미 존재합니다.");
    }

    @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 저장한다.")
    @Test
    void saveWithExistentColor() {
        String color = "color";
        lineRepository.save(new Line("신분당선", color));
        assertThatThrownBy(() -> lineRepository.save(new Line("분당선", color)))
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
        lines.forEach(lineRepository::save);
        assertThat(lineRepository.findAll()).hasSize(2);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findById() {
        String name = "신분당선";
        String color = "color";
        Long lineId = lineRepository.save(new Line(name, color));
        Line line = lineRepository.findById(lineId);
        assertAll(() -> {
            assertThat(line.getName()).isEqualTo(name);
            assertThat(line.getColor()).isEqualTo(color);
        });
    }

    @DisplayName("존재하지 않는 지하철노선을 조회한다.")
    @Test
    void findWithNonexistentId() {
        assertThatThrownBy(() -> lineRepository.findById(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("조회하고자 하는 지하철노선이 존재하지 않습니다.");
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void update() {
        Long lineId = lineRepository.save(new Line("신분당선", "color1"));
        Line line = lineRepository.findById(lineId);
        line.update("분당선", "color2");

        lineRepository.update(line);
        Line updatedLine = lineRepository.findById(lineId);

        assertAll(() -> {
            assertThat(updatedLine.getName()).isEqualTo("분당선");
            assertThat(updatedLine.getColor()).isEqualTo("color2");
        });
    }

    @DisplayName("존재하지 않는 지하철노선을 수정한다.")
    @Test
    void updateWithNonexistentId() {
        assertThatThrownBy(() -> lineRepository.update(new Line(1L, "신분당선", "color")))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("수정하고자 하는 지하철노선이 존재하지 않습니다.");
    }

    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void remove() {
        Long lineId = lineRepository.save(new Line("신분당선", "color"));
        lineRepository.remove(lineId);
        assertThat(lineRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 지하철노선을 삭제한다.")
    @Test
    void removeWithNonexistentId() {
        assertThatThrownBy(() -> lineRepository.remove(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("삭제하고자 하는 지하철노선이 존재하지 않습니다.");
    }
}