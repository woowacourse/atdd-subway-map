package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.domain.Line;

import static org.assertj.core.api.Assertions.assertThat;

public class LineDaoTest {

    private LineDao lineDao = Assembler.getLineDao();

    @Test
    @DisplayName("노선을 등록한다.")
    void save() {
        Line expected = new Line("신분당선", "red");
        Line actual = lineDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }
}
