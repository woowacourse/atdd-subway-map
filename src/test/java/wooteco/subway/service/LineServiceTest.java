package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

public class LineServiceTest {

    private final LineService lineService = LineService.getInstance();

    @AfterEach
    void tearDown() {
        LineDao.getInstance().deleteAll();
    }

    @Test
    @DisplayName("Line 을 저장한다.")
    void save() {
        //given
        Line line = new Line("가산디지털단지", "blue");

        //when
        Line savedLine = lineService.save(line);

        //then
        assertThat(equals(savedLine, line)).isTrue();
    }

    private boolean equals(Line lineA, Line lineB) {
        return lineA.getColor().equals(lineB.getColor()) && lineA.getName().equals(lineB.getName());
    }

    @Test
    @DisplayName("중복된 역을 저장할 수 없다.")
    void saveDuplicateName() {
        //given
        Line line = new Line("중곡", "khaki");
        lineService.save(line);

        //then
        assertThatThrownBy(() -> lineService.save(line))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 등록된 노선입니다.");
    }
}
