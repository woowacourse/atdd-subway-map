package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
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
    }

    @Test
    void save() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line);

        assertThat(savedLine).isNotNull();
    }

    @Test
    void findAll() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-green-600");

        lineDao.save(line1);
        lineDao.save(line2);

        final List<Line> lines = lineDao.findAll();
        final List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        final List<String> lineColors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(lineNames).containsAll(List.of("신분당선", "분당선")),
                () -> assertThat(lineColors).containsAll(List.of("bg-red-600", "bg-green-600"))
        );
    }

    @Test
    void findById() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line expected = lineDao.save(line1);
        final Line actual = lineDao.findById(expected.getId()).get();

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(expected.getColor())
        );
    }

    @Test
    void update() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line1);
        final Long savedId = savedLine.getId();

        final String newLineName = "새로운 노선";
        final String newLineColor = "bg-red-500";
        final Line newLine = new Line(newLineName, newLineColor);
        lineDao.update(savedId, newLine);

        assertAll(
                () -> assertThat(lineDao.findById(savedId)).isPresent(),
                () -> assertThat(lineDao.findById(savedId).get().getName()).isEqualTo(newLineName),
                () -> assertThat(lineDao.findById(savedId).get().getColor()).isEqualTo(newLineColor)
        );
    }

    @Test
    void deleteById() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line savedLine = lineDao.save(line1);

        lineDao.deleteById(savedLine.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
