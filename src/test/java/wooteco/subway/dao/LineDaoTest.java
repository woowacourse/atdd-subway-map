package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 추가한다")
    void save() {
        Line line = lineDao.save(new Line("1호선", "blue"));

        assertThat(line.getName()).isEqualTo("1호선");
        assertThat(line.getColor()).isEqualTo("blue");
    }

    @Test
    @DisplayName("특정 노선 조회")
    void findById() {
        Line line = lineDao.save(new Line("1호선", "blue"));
        assertThat(lineDao.findById(line.getId())).isEqualTo(line);
    }

    @Test
    void findAll() {
        Line line1 = lineDao.save(new Line("1호선", "blue"));
        Line line2 = lineDao.save(new Line("2호선", "green"));

        List<Line> lines = lineDao.findAll();

        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).contains(line1),
                () -> assertThat(lines).contains(line2)
        );
    }

    @Test
    void updateById() {
        Line savedLine = lineDao.save(new Line("1호선", "blue"));
        Long id = savedLine.getId();

        lineDao.update(id, "2호선", "black");

        assertThat(lineDao.findById(id)).isEqualTo(new Line(id, "2호선", "black"));
    }

    @Test
    void deleteById() {
        Line line = lineDao.save(new Line("1호선", "blue"));

        lineDao.deleteById(line.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}