package wooteco.subway.dao.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.line.Line;

@JdbcTest
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
    }

    @Test
    void dependency() {
        assertThat(lineDao).isNotNull();
    }

    @Test
    void save() {
        // given
        Line line = new Line("3호선", "bg-blue-500");

        // when
        Line persistedLine = lineDao.save(line);

        // then
        assertAll(
            () -> assertThat(line.getName()).isEqualTo(persistedLine.getName()),
            () -> assertThat(line.getColor()).isEqualTo(persistedLine.getColor())
        );
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("3호선", "bg-blue-600");
        Line line2 = new Line("4호선", "bg-yellow-600");

        // when
        lineDao.save(line1);
        lineDao.save(line2);
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines)
            .extracting("name", "color")
            .containsExactlyInAnyOrder(
                Tuple.tuple(line1.getName(), line1.getColor()),
                Tuple.tuple(line2.getName(), line2.getColor())
            );
    }

    @Test
    void findById() {
        // given
        Line line = new Line("3호선", "bg-red-600");

        // when
        Line persistedLine = lineDao.save(line);
        Line selectedLine = lineDao.findById(persistedLine.getId()).get();

        // then
        assertAll(
            () -> assertThat(selectedLine.getId()).isEqualTo(persistedLine.getId()),
            () -> assertThat(selectedLine.getName()).isEqualTo(persistedLine.getName()),
            () -> assertThat(selectedLine.getColor()).isEqualTo(persistedLine.getColor())
        );
    }

    @Test
    void existsByName() {
        // given
        String name = "3호선";
        Line line = new Line(name, "bg-blue-600");

        // when
        lineDao.save(line);

        // then
        assertAll(
            () -> assertThat(lineDao.existsByName(name)).isTrue(),
            () -> assertThat(lineDao.existsByName("0호선")).isFalse()
        );
    }

    @Test
    void existsById() {
        // given
        Line line = new Line("3호선", "bg-blue-600");

        // when
        Line persistedLine = lineDao.save(line);

        // then
        assertAll(
            () -> assertThat(lineDao.existsById(persistedLine.getId())).isTrue(),
            () -> assertThat(lineDao.existsById(-1L)).isFalse()
        );
    }

    @Test
    void update() {
        // given
        Line line = new Line("3호선", "bg-red-600");

        // when
        Line persistedLine = lineDao.save(line);
        lineDao.update(new Line(persistedLine.getId(), "4호선", "bg-blue-600"));
        Line updatedLine = lineDao.findById(persistedLine.getId()).get();

        // then
        assertAll(
            () -> assertThat(persistedLine.getId()).isEqualTo(updatedLine.getId()),
            () -> assertThat("4호선").isEqualTo(updatedLine.getName()),
            () -> assertThat("bg-blue-600").isEqualTo(updatedLine.getColor())
        );
    }

    @Test
    void deleteById() {
        // given
        Line line = new Line("3호선", "bg-black-600");
        Line persistedLine = lineDao.save(line);

        // when
        lineDao.deleteById(persistedLine.getId());

        // then
        assertThat(lineDao.findById(persistedLine.getId()).isPresent()).isFalse();
    }
}
