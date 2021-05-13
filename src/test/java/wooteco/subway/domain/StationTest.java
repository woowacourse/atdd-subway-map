package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {

    @DisplayName("ID가 같으면 같은 역으로 취급한다.")
    @Test
    void equals() {
        assertThat(new Station(1L, "왕십리역")).isEqualTo(new Station(1L, "답십리역"));
    }

    @DisplayName("ID가 다르면 다른 역으로 취급한다.")
    @Test
    void notEquals() {
        assertThat(new Station(1L, "왕십리역")).isNotEqualTo(new Station(2L, "왕십리역"));
    }
}
