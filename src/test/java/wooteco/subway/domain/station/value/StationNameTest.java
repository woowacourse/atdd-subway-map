package wooteco.subway.domain.station.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class StationNameTest {

    @DisplayName("stationName은 1글자 이상이어야 한다.")
    @Test
    void stationName() {
        assertThatThrownBy(() -> new StationName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("글자 수는 0보다 커야 합니다.");
    }

    @Test
    void asString() {
        assertThat(new StationName("A").asString()).isEqualTo("A");
    }

}