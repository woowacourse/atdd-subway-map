package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    private static final LineDao lineDao = new LineDao();

    @Test
    @DisplayName("노선을 등록할 수 있다.")
    void save() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        final Long savedId = lineDao.save(line);

        // then
        final Line findLine = lineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }
}
