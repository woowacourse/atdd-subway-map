package wooteco.subway.line.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.Line;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    private Line line;

    @BeforeEach
    void setUp() {
        line = lineDao.save(new Line("3호선", "bg-blue-500"));
    }

    @Test
    void save() {
        assertThat(line).isEqualTo(new Line("3호선", "bg-blue-500"));
    }

    @Test
    void saveDuplicate() {
        assertThatThrownBy(() -> lineDao.save(new Line("3호선", "bg-yellow-600")))
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAll() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines.get(0)).isEqualTo(new Line("3호선", "bg-blue-500"));
    }

    @Test
    void findById() {
        Line selectedLine = lineDao.findById(line.getId());

        assertAll(
            () -> assertThat(selectedLine.getId()).isEqualTo(line.getId()),
            () -> assertThat(selectedLine.getName()).isEqualTo(line.getName()),
            () -> assertThat(selectedLine.getColor()).isEqualTo(line.getColor())
        );
    }

    @Test
    void update() {
        lineDao.update(line.getId(), "8호선", "bg-blue-600");
        Line updatedLine = lineDao.findById(line.getId());

        assertAll(
            () -> assertThat(line.getId()).isEqualTo(updatedLine.getId()),
            () -> assertThat("8호선").isEqualTo(updatedLine.getName()),
            () -> assertThat("bg-blue-600").isEqualTo(updatedLine.getColor())
        );
    }

    @Test
    void deleteById() {
        lineDao.deleteById(line.getId());

        assertThatThrownBy(() -> lineDao.findById(line.getId()))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
