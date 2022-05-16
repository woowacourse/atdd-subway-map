package wooteco.subway.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class StationTest {

    @Test
    void checkNullOrEmpty() {
        assertThatThrownBy(() -> new Station(1L, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Station(1L, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}