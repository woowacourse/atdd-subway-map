package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.exception.RowDuplicatedException;

class LineSeriesTest {

    @Test
    @DisplayName("중복된 이름인 경우 예외를 던진다.")
    public void throwsExceptionWithDuplicatedName() {
        // given
        LineSeries series = new LineSeries(List.of(
            new Line("first", "color1"),
            new Line("second", "color2"))
        );
        // when
        // then
        assertThatExceptionOfType(RowDuplicatedException.class)
            .isThrownBy(() -> series.create("first", "color3"));
    }
}