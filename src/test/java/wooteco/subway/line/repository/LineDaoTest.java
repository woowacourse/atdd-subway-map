package wooteco.subway.line.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.domain.Line;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;
    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);

        jdbcTemplate.update("ALTER TABLE LINE ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM LINE");
    }

    @Test
    void save() {
        lineDao.save(new Line("신분당선", "red"));

        Line line = lineDao.findById(1L);
        Line expected = new Line(1L, "신분당선", "red");

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void allLines() {
        lineDao.save(new Line("강남역", "red"));
        lineDao.save(new Line("신분당역", "red"));
        lineDao.save(new Line("잠실역", "green"));

        List<Line> lines = lineDao.allLines();

        List<Line> expected = Arrays.asList(
                new Line(1L, "강남역", "red"),
                new Line(2L, "신분당역", "red"),
                new Line(3L, "잠실역", "green")
        );

        assertThat(lines).containsAll(expected);
    }

    @Test
    void findById() {
        lineDao.save(new Line("신분당선", "red"));

        Line line = lineDao.findById(1L);
        Line expected = new Line(1L, "신분당선", "red");

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void update() {
        lineDao.save(new Line("신분당선", "red"));

        lineDao.update(new Line(1L, "강남역", "red"));

        Line line = lineDao.findById(1L);
        Line expected = new Line(1L, "강남역", "red");

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void deleteById() {
        lineDao.save(new Line("강남역", "red"));

        lineDao.deleteById(1L);

        assertThatThrownBy(() -> lineDao.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}