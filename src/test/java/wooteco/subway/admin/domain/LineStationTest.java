package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineStationTest {
    private LineStation lineStation0to1 = new LineStation(null, 1L, 2, 2);
    private LineStation lineStation1to2 = new LineStation(1L, 2L, 2, 2);
    private LineStation lineStation2to3 = new LineStation(2L, 3L, 2, 2);
    private LineStation lineStation2to4 = new LineStation(2L, 4L, 2, 2);

    @DisplayName("첫번째 구간인지 제대로 확인하는지")
    @Test
    void firstLineStation() {
        assertThat(lineStation0to1.isStartStation()).isEqualTo(true);
    }

    @DisplayName("같은 전 역 id를 가지고 있는지 제대로 확인해주는지")
    @Test
    void hasSamePreStationIdWith() {
        assertThat(lineStation2to3.hasSamePreStationIdWith(lineStation2to4)).isTrue();
    }

    @DisplayName("전 역 id를 주어진 구간의 역 id로 제대로 업데이트해주는지")
    @Test
    void updatePreStationIdWithIdOf() {
        lineStation1to2.updatePreStationIdWithIdOf(lineStation2to4);
        assertThat(lineStation1to2.getPreStationId()).isEqualTo(4L);
    }

    @DisplayName("전 역 id를 주어진 id로 제대로 업데이트해주는지")
    @Test
    void updatePreStationId() {
        lineStation0to1.updatePreLineStation(100L);
        assertThat(lineStation0to1.getPreStationId()).isEqualTo(100L);
    }
}
