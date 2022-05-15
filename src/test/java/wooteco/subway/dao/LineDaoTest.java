package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;

@Sql("/sql/schema-test.sql")
@JdbcTest
public class LineDaoTest {

    private static final Line FIRST_LINE = new Line("2호선", "GREEN");
    private static final Line SECOND_LINE = new Line("3호선", "ORANGE");

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
        Line result = lineDao.save(FIRST_LINE);

        assertAll(
            () -> assertNotNull(result.getId()),
            () -> assertThat(result.getName()).isEqualTo("2호선"),
            () -> assertThat(result.getColor()).isEqualTo("GREEN")
        );
    }

    @DisplayName("지하철 노선 이름을 이용해 지하철 노선을 조회한다.")
    @Test
    void findByName() {
        lineDao.save(FIRST_LINE);
        Line result = lineDao.findByName("2호선").orElse(null);

        assertAll(
            () -> assertThat(result.getName()).isEqualTo("2호선"),
            () -> assertThat(result.getColor()).isEqualTo("GREEN")
        );
    }

    @DisplayName("존재하지 않는 지하철 노선 이름을 이용해 지하철 노선을 조회하면 Empty를 반환한다.")
    @Test
    void findByName_empty() {
        lineDao.save(FIRST_LINE);

        Optional<Line> result = lineDao.findByName("없는이름");

        assertThat(result).isEmpty();
    }

    @DisplayName("저장된 모든 지하철 노선을 조회한다.")
    @Test
    void findAll() {
        lineDao.save(FIRST_LINE);
        lineDao.save(SECOND_LINE);

        List<Line> lines = lineDao.findAll();
        assertAll(
            () -> assertThat(lines.size()).isEqualTo(2),
            () -> assertThat(lines.get(0).getId()).isEqualTo(1),
            () -> assertThat(lines.get(0).getName()).isEqualTo("2호선"),
            () -> assertThat(lines.get(0).getColor()).isEqualTo("GREEN"),
            () -> assertThat(lines.get(1).getId()).isEqualTo(2),
            () -> assertThat(lines.get(1).getName()).isEqualTo("3호선"),
            () -> assertThat(lines.get(1).getColor()).isEqualTo("ORANGE")
        );
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void findById() {
        lineDao.save(FIRST_LINE);
        Line line = lineDao.findById(1L).get();
        assertAll(
            () -> assertThat(line.getId()).isEqualTo(1),
            () -> assertThat(line.getName()).isEqualTo("2호선"),
            () -> assertThat(line.getColor()).isEqualTo("GREEN")
        );
    }

    @DisplayName("존재하지 않는 id를 이용해 지하철 노선을 조회면 Empty를 반환한다.")
    @Test
    void findById_empty() {
        lineDao.save(FIRST_LINE);

        Optional<Line> lineEmpty = lineDao.findById(1000L);

        assertThat(lineEmpty).isEmpty();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        Line savedLine = lineDao.save(FIRST_LINE);

        lineDao.save(SECOND_LINE);
        lineDao.delete(savedLine);

        List<Line> result = lineDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        Line savedLine = lineDao.save(FIRST_LINE);
        Line updateLine = new Line(savedLine.getId(), SECOND_LINE.getName(), SECOND_LINE.getColor());

        lineDao.update(savedLine, updateLine);

        Line result = lineDao.findById(1L).get();

        assertAll(
            () -> assertThat(result.getId()).isEqualTo(1L),
            () -> assertThat(result.getName()).isEqualTo("3호선"),
            () -> assertThat(result.getColor()).isEqualTo("ORANGE")
        );
    }
}
