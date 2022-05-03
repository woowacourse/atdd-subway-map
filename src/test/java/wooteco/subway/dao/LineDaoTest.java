package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {


    @BeforeEach
    void setUp() {
        LineDao.deleteAllLines();
    }

    @DisplayName("라인을 저장한다.")
    @Test
    void lineSaveTest() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        assertThat(savedLine.getId()).isEqualTo(1);
    }

    @DisplayName("전체 라인을 조회한다.")
    @Test
    void findAllLines() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        assertThat(LineDao.findAllLines()).hasSize(1);
    }
}
