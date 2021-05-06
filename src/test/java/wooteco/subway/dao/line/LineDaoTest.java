package wooteco.subway.dao.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.line.Line;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@Transactional
class LineDaoTest {

    @Autowired
    @Qualifier("jdbc")
    private LineDao lineDao;

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
        assertAll(
            () -> assertThat(lines.get(0).getName()).isEqualTo(line1.getName()),
            () -> assertThat(lines.get(0).getColor()).isEqualTo(line1.getColor()),
            () -> assertThat(lines.get(1).getName()).isEqualTo(line2.getName()),
            () -> assertThat(lines.get(1).getColor()).isEqualTo(line2.getColor())
        );
    }

    @Test
    void findById() {
        // given
        Line line = new Line("3호선", "bg-red-600");

        // when
        Line persistedLine = lineDao.save(line);
        Line selectedLine = lineDao.findById(persistedLine.getId());

        // then
        assertAll(
            () -> assertThat(selectedLine.getId()).isEqualTo(persistedLine.getId()),
            () -> assertThat(selectedLine.getName()).isEqualTo(persistedLine.getName()),
            () -> assertThat(selectedLine.getColor()).isEqualTo(persistedLine.getColor())
        );
    }

    @Test
    void update() {
        // given
        Line line = new Line("3호선", "bg-red-600");

        // when
        Line persistedLine = lineDao.save(line);
        lineDao.update(new Line(persistedLine.getId(), "4호선", "bg-blue-600"));
        Line updatedLine = lineDao.findById(persistedLine.getId());

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
        assertThatThrownBy(() -> lineDao.findById(persistedLine.getId()))
            .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
