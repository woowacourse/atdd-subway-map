package wooteco.subway.line;

import org.junit.jupiter.api.Test;
import wooteco.subway.AppConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class LineDaoTest {
    private LineRepository lineRepository = AppConfig.lineRepository();

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
}
