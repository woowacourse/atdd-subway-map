package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Assertions;
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
    private LineDaoImpl lineDao;

    @BeforeEach
    void beforeEach() {
        lineDao = new LineDaoImpl(jdbcTemplate);
    }

    @Test
    void save() {
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = lineDao.insert(new Line(name, color));

        assertThat(line.getName()).isEqualTo(name);
    }

    @Test
    void findById() {
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = lineDao.insert(new Line(name, color));

        Line foundLine = lineDao.findById(line.getId());

        assertThat(foundLine.getName()).isEqualTo(name);
    }

    @Test
    void findAll() {
        lineDao.insert(new Line("신분당선", "bg-red-600"));
        lineDao.insert(new Line("2호선", "bg-blue-500"));

        List<Line> lines = lineDao.findAll();
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    void update() {
        Line line = lineDao.insert(new Line("신분당선", "bg-red-600"));

        Long id = line.getId();
        String updateName = "2호선";
        String updateColor = "bg-blue-500";
        lineDao.update(id, updateName, updateColor);

        Line updatedLine = lineDao.findById(id);
        Assertions.assertAll(
                () -> assertThat(updatedLine.getName()).isEqualTo(updateName),
                () -> assertThat(updatedLine.getColor()).isEqualTo(updateColor)
        );
    }

    @Test
    void delete() {
        String name = "신분당선";
        String color = "bg-red-600";
        Line line = lineDao.insert(new Line(name, color));

        lineDao.delete(line.getId());
        assertThat(lineDao.findAll()).isEmpty();
    }
}
