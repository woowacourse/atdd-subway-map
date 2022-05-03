package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @DisplayName("라인을 저장한다.")
    @Test
    void lineSaveTest() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        assertThat(savedLine.getId()).isEqualTo(1);
    }
}
