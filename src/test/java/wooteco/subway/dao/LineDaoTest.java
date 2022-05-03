package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        Line line = new Line("신분당선", "bg-red-600");
        Line savedLine = lineDao.save(line);

        assertThat(savedLine).isNotNull();
    }

    @Test
    void findAll() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line line2 = new Line("분당선", "bg-green-600");

        lineDao.save(line1);
        lineDao.save(line2);

        List<Line> lines = lineDao.findAll();
        List<String> lineNames = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        List<String> lineColors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(lineNames).containsAll(List.of("신분당선", "분당선")),
                () -> assertThat(lineColors).containsAll(List.of("bg-red-600", "bg-green-600"))
        );
    }

    @Test
    void findById() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line expected = lineDao.save(line1);
        Line actual = lineDao.findById(expected.getId());

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(expected.getColor())
        );
    }

    @Test
    void update() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line savedLine = lineDao.save(line1);
        Long savedId = savedLine.getId();

        String newLineName = "새로운 노선";
        String newLineColor = "bg-red-500";
        Line newLine = new Line(newLineName, newLineColor);
        lineDao.update(savedId, newLine);

        assertAll(
                () -> assertThat(lineDao.findById(savedId).getName()).isEqualTo(newLineName),
                () -> assertThat(lineDao.findById(savedId).getColor()).isEqualTo(newLineColor)
        );
    }

    @Test
    void deleteById() {
        Line line1 = new Line("신분당선", "bg-red-600");
        Line savedLine = lineDao.save(line1);

        lineDao.deleteById(savedLine.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}
