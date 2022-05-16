package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("구간의 시점과 종점이 같은 역이면 예외를 반환한다.")
    @Test
    void sameEndpoint() {
        Station station = new Station("강남역");
        assertThatThrownBy(() -> new Section(station, station, 1));
    }

}
