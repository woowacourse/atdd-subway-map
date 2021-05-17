package wooteco.subway.domain.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    void create() {
        // given
        Station station1 = new Station(0L, "잠실역");
        Station station2 = new Station(0L, "잠실역");

        // then
        assertThat(station1).isEqualTo(station2);
    }

    @DisplayName("지하철 역 이름은 null이나 빈칸이 될 수 없습니다.")
    @Test
    void name() {
        // given, when
        String emptyName = "";

        // then
        assertAll(
            () -> assertThatThrownBy(() -> new Station(null))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Station(emptyName))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }
}
