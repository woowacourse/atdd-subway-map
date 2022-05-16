package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.fixture.LineFixture;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineSeries;
import wooteco.subway.exception.RowDuplicatedException;

class LineSeriesTest {

    @Test
    @DisplayName("중복된 이름인 경우 예외를 던진다.")
    public void throwsExceptionWithDuplicatedName() {
        // given
        LineSeries series = new LineSeries(List.of(LineFixture.getLineAb()));
        // when
        Line line = new Line("분당선", "color3");
        // then
        assertThatExceptionOfType(RowDuplicatedException.class)
            .isThrownBy(() -> series.add(line));
    }
}