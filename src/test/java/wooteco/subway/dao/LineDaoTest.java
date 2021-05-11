package wooteco.subway.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.Line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineDaoTest extends AcceptanceTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void create() {
        // given
        Line line = new Line("2호선", "color name");

        // when
        lineRepository.save(line);

        // then
        assertThat(lineRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void findAll() {
        // given
        Line line1 = new Line("1호선", "color name");
        Line line2 = new Line("9호선", "color name");
        int sizeBefore = lineRepository.findAll().size();

        // when
        lineRepository.save(line1);
        lineRepository.save(line2);

        // then
        assertThat(lineRepository.findAll().size()).isEqualTo(sizeBefore + 2);
    }

    @Test
    void findByIdTest() {
        // given
        Line line = new Line("7호선", "color name");

        // when
        Line expected = lineRepository.save(line);
        Line found = lineRepository.findById(expected.getId());

        // then
        assertThat(expected).isEqualTo(found);
    }

    @Test
    void update() {
        // given
        Line line = new Line("7호선", "color name");
        Line expected = lineRepository.save(line);
        Line newLine = new Line("2호선", "new color");

        // when
        lineRepository.update(expected.getId(), newLine);

        //then
        assertThat(lineRepository.findById(expected.getId())).isEqualTo(newLine);
    }

    @Test
    void removeTest() {
        // given
        Line line = new Line("7호선", "color name");
        Long id = lineRepository.save(line).getId();

        // when
        lineRepository.delete(id);

        // then
        assertThatThrownBy(() -> lineRepository.findById(id))
                .isInstanceOf(DataAccessException.class);
    }
}
