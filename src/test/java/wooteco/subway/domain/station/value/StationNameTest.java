package wooteco.subway.domain.station.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.station.NameLengthException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationNameTest {

    @DisplayName("stationName은 1글자 이상이어야 한다.")
    @Test
    void stationName() {
        assertThatThrownBy(() -> new StationName(""))
                .isInstanceOf(NameLengthException.class)
                .hasMessage("이름은 0 보다 커야 합니다.");
    }

    @Test
    void asString() {
        assertThat(new StationName("A").asString()).isEqualTo("A");
    }

}