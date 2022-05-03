package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Line;

public class LineDaoTest {

    private final LineDao lineDao = LineDao.getInstance();

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        //given
        Line line = new Line("가산디지털", "khaki");

        //when
        Line actual = lineDao.save(line);

        //then
        assertAll(
            () -> assertThat(actual.getName()).isEqualTo(line.getName()),
            () -> assertThat(actual.getColor()).isEqualTo(line.getColor())
        );
    }
}
