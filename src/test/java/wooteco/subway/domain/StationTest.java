package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StationTest {

    @Test
    @DisplayName("id가 동일하다면 true 반환")
    void isSameId() {
        assertThat(new Station(1L, "name").isSameId(1L)).isTrue();
    }
}
