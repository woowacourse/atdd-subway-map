package wooteco.subway.repository.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.dao.LineDao;

@JdbcTest
class JdbcLineDaoTest {

    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        this.lineDao = new JdbcLineDao(dataSource);
    }

    @DisplayName("지하철노선을 저장한다.")
    @Test
    void save() {
        Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        assertThat(lineId).isGreaterThan(0);
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAll() {
        List<Line> lines = List.of(
                new Line("신분당선", "bg-red-600"),
                new Line("1호선", "bg-red-601"),
                new Line("2호선", "bg-red-602")
        );
        lines.forEach(lineDao::save);
        List<Line> foundLines = lineDao.findAll();
        assertThat(foundLines).hasSize(3);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findById() {
        Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        Optional<Line> line = lineDao.findById(lineId);
        assertThat(line.isPresent()).isTrue();
    }

    @DisplayName("존재하지 않는 지하철노선을 조회한다.")
    @Test
    void findWithNonexistentId() {
        Optional<Line> line = lineDao.findById(1L);
        assertThat(line.isEmpty()).isTrue();
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void update() {
        Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.update(lineId, "분당선", "bg-blue-600");
        Optional<Line> updatedLine = lineDao.findById(lineId);

        assertAll(() -> {
            assertThat(updatedLine.isPresent()).isTrue();

            Line line = updatedLine.get();
            assertThat(line.getId()).isEqualTo(lineId);
            assertThat(line.getName()).isEqualTo("분당선");
            assertThat(line.getColor()).isEqualTo("bg-blue-600");
        });
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void remove() {
        Long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.remove(lineId);

        assertThat(lineDao.findAll()).isEmpty();
    }

    @DisplayName("해당 이름의 노선이 존재하는지 확인한다.")
    @Test
    void existsByName() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        assertThat(lineDao.existsByName("신분당선")).isTrue();
    }

    @DisplayName("해당 색상의 노선이 존재하는지 확인한다.")
    @Test
    void existsByColor() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        assertThat(lineDao.existsByColor("bg-red-600")).isTrue();
    }
}
