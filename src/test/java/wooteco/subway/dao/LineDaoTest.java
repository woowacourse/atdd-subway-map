package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);

        List<Line> lines = lineDao.findAll();
        List<Long> lineIds = lines.stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            lineDao.deleteById(lineId);
        }
    }

    @Test
    void save() {
        // given
        Line line = new Line("1호선", "bg-red-600");

        // when
        Long savedId = lineDao.save(line);
        Line line1 = lineDao.findById(savedId);

        // then
        assertThat(line.getName()).isEqualTo(line1.getName());
    }

    @Test
    void validateDuplication() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("1호선", "bg-red-600");

        // when
        lineDao.save(line1);

        // then
        assertThatThrownBy(() -> lineDao.save(line2))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", "bg-red-600");
        Line line2 = new Line("2호선", "bg-green-600");

        // when
        lineDao.save(line1);
        lineDao.save(line2);

        // then
        List<String> names = lineDao.findAll()
            .stream()
            .map(Line::getName)
            .collect(Collectors.toList());

        assertThat(names)
            .hasSize(2)
            .contains(line1.getName(), line2.getName());
    }

    @Test
    void delete() {
        // given
        Line line = new Line("1호선", "bg-red-600");
        Long savedId = lineDao.save(line);

        // when
        lineDao.deleteById(savedId);

        // then
        List<Long> lineIds = lineDao.findAll()
            .stream()
            .map(Line::getId)
            .collect(Collectors.toList());

        assertThat(lineIds)
            .hasSize(0)
            .doesNotContain(savedId);
    }

    @Test
    void update() {
        // given
        Line originLine = new Line("1호선", "bg-red-600");
        Long savedId = lineDao.save(originLine);

        // when
        Line newLine = new Line("2호선", "bg-green-600");
        lineDao.updateById(savedId, newLine);
        Line line = lineDao.findById(savedId);

        // then
        assertThat(line).isEqualTo(newLine);
    }
}
