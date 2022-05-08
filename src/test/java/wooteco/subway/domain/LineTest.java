package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("두 개의 역이 노선에 포함되어 있으면 참을 반환한다")
    @Test
    void containsBothReturnsTrue() {
        // given
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "시청");
        Line line = new Line(1L, "1호선", "blue", new Section(1L, station1, station2, 10));

        // when & then
        assertThat(line.containsBoth(station1, station2)).isTrue();
    }

    @DisplayName("두 개의 역이 노선에 포함되어 있지 않으면 거짓을 반환한다")
    @Test
    void containsBothReturnsFalse() {
        // given
        Station station1 = new Station(1L, "서울역");
        Station station2 = new Station(2L, "시청");
        Station station3 = new Station(3L, "성수");
        Line line = new Line(1L, "1호선", "blue", new Section(1L, station1, station2, 10));

        // when & then
        assertThat(line.containsBoth(station2, station3)).isFalse();
    }
}
