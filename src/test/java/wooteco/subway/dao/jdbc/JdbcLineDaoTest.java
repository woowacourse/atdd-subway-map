package wooteco.subway.dao.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class JdbcLineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 추가한다")
    void save() {
        Line line = lineDao.create(new Line("1호선", "blue"));

        assertAll(
                () -> assertThat(line.getName()).isEqualTo("1호선"),
                () -> assertThat(line.getColor()).isEqualTo("blue")
        );
    }

    @Test
    @DisplayName("특정 노선 조회")
    void findById() {
        Line line = lineDao.create(new Line("1호선", "blue"));
        assertThat(lineDao.findById(line.getId())).isEqualTo(line);
    }

    @Test
    void findAll() {
        Line line1 = lineDao.create(new Line("1호선", "blue"));
        Line line2 = lineDao.create(new Line("2호선", "green"));

        List<Line> lines = lineDao.findAll();

        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).contains(line1),
                () -> assertThat(lines).contains(line2)
        );
    }

    @Test
    void updateById() {
        Line savedLine = lineDao.create(new Line("1호선", "blue"));
        Long id = savedLine.getId();

        lineDao.update(id, "2호선", "black");

        assertThat(lineDao.findById(id)).isEqualTo(new Line(id, "2호선", "black"));
    }

    @Test
    void deleteById() {
        Line line = lineDao.create(new Line("1호선", "blue"));

        lineDao.deleteById(line.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}