package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("이름이 같은지 확인한다.")
    public void hasSameNameWith() {
        // given
        final Station upStation = new Station("상행역");
        final Station downStation = new Station("하행역");
        final Line line = new Line("신분당선", "bg-red-600", upStation, downStation);
        // when
        final boolean hasSameName = line.hasSameNameWith(new Line("신분당선", "bg-red-600", upStation, downStation));
        // then
        assertThat(hasSameName).isTrue();
    }
}
