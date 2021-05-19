package wooteco.subway.infrastructure.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.line.Line;
import wooteco.subway.infrastructure.line.LineDao;
import wooteco.util.LineFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql("classpath:/line/lineQueryInit.sql")
@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @Test
    void save() {
        lineDao.save(
                LineFactory.create("신분당선", "red", Collections.emptyList())
        );

        Line line = lineDao.findById(1L);
        Line expected = LineFactory.create(1L, "신분당선", "red", Collections.emptyList());

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void allLines() {
        lineDao.save(LineFactory.create("강남역", "red", Collections.emptyList()));
        lineDao.save(LineFactory.create("신분당선", "red", Collections.emptyList()));
        lineDao.save(LineFactory.create("잠실역", "green", Collections.emptyList()));

        List<Line> lines = lineDao.allLines();

        List<Line> expected = Arrays.asList(
                LineFactory.create(1L, "강남역", "red", Collections.emptyList()),
                LineFactory.create(2L, "신분당선", "red", Collections.emptyList()),
                LineFactory.create(3L, "잠실역", "green", Collections.emptyList())
        );

        assertThat(lines).containsAll(expected);
    }

    @Test
    void findById() {
        lineDao.save(LineFactory.create("신분당선", "red", Collections.emptyList()));

        Line line = lineDao.findById(1L);
        Line expected = LineFactory.create(1L, "신분당선", "red", Collections.emptyList());

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void update() {
        Line savedLine = lineDao.save(LineFactory.create("신분당선", "red", Collections.emptyList()));
        lineDao.update(LineFactory.create(savedLine.getLineId(), "강남역", "yellow", Collections.emptyList()));

        Line line = lineDao.findById(1L);
        Line expected = LineFactory.create(1L, "강남역", "yellow", Collections.emptyList());

        assertThat(line).isEqualTo(expected);
    }

    @Test
    void deleteById() {
        lineDao.save(LineFactory.create("강남역", "red", Collections.emptyList()));

        lineDao.deleteById(1L);

        assertThatThrownBy(() -> lineDao.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

}