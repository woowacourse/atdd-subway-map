package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineServiceTest {

    @Test
    void saveLine() {
        LineService lineService = new LineService();

        Line line = lineService.save("신분당선", "bg-red-600");

        assertThat(LineDao.findById(line.getId())).isNotEmpty();
    }
}
