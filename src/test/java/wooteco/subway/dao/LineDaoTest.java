package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void beforeEach() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("새 지하철 노선을 저장한다.")
    @Test
    void save() {
        Line testLine = new Line("save", "GREEN");
        Line result = lineDao.save(testLine);
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("save"),
                () -> assertThat(result.getColor()).isEqualTo("GREEN")
        );
    }

    @DisplayName("지하철 노선 이름을 이용해 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        Line test = new Line("test", "GREEN");
        lineDao.save(test);
        Line result = lineDao.findByName("test").orElse(null);
        Optional<Line> result2 = lineDao.findByName("test2");
        assertAll(
                () -> assertThat(result.getName()).isEqualTo("test"),
                () -> assertThat(result.getColor()).isEqualTo("GREEN"),
                () -> assertThat(result2).isEmpty()
        );
    }

    @DisplayName("저장된 모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line test1 = new Line("test1", "GREEN");
        Line test2 = new Line("test2", "YELLOW");
        lineDao.save(test1);
        lineDao.save(test2);

        List<Line> lines = lineDao.findAll();

        assertAll(
                () -> assertThat(lines.size()).isEqualTo(2),
                () -> assertThat(lines.get(0).getId()).isNotNull(),
                () -> assertThat(lines.get(0).getName()).isEqualTo("test1"),
                () -> assertThat(lines.get(0).getColor()).isEqualTo("GREEN"),
                () -> assertThat(lines.get(1).getId()).isNotNull(),
                () -> assertThat(lines.get(1).getName()).isEqualTo("test2"),
                () -> assertThat(lines.get(1).getColor()).isEqualTo("YELLOW")
        );
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void findById() {
        Line savedLine = lineDao.save(new Line("test1", "GREEN"));
        Line line = lineDao.findById(savedLine.getId()).get();
        assertAll(
                () -> assertThat(line.getId()).isEqualTo(savedLine.getId()),
                () -> assertThat(line.getName()).isEqualTo("test1"),
                () -> assertThat(line.getColor()).isEqualTo("GREEN")
        );
    }

    @DisplayName("id를 이용해 지하철 노선을 조회할 때 없으면 empty를 반환한다.")
    @Test
    void findById_noSuchId() {
        Optional<Line> lineEmpty = lineDao.findById(1000L);
        assertThat(lineEmpty).isEmpty();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        Line test = new Line("test1", "YELLOW");
        Line savedTest = lineDao.save(test);
        Line test2 = new Line("test2", "BROWN");
        lineDao.save(test2);

        lineDao.delete(savedTest);

        List<Line> result = lineDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        Line line = new Line("test1", "YELLOW");
        Line savedLine = lineDao.save(line);
        Line newLine = new Line(savedLine.getId(), "test2", "BROWN");

        lineDao.update(savedLine, newLine);

        Line result = lineDao.findById(savedLine.getId()).get();
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(savedLine.getId()),
                () -> assertThat(result.getName()).isEqualTo("test2"),
                () -> assertThat(result.getColor()).isEqualTo("BROWN")
        );
    }
}
