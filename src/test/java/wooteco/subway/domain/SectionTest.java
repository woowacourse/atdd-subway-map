package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("거리가 1 미만인 구간은 생성할 수 없다.")
    @Test
    void cannotCreateDistanceLessThanOneSection() {
        Station upStation = new Station("서울역");
        Station downStation = new Station("시청");
        int distance = 0;

        Assertions.assertThatThrownBy(() -> new Section(1L, upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리가 1 미만");
    }

}
