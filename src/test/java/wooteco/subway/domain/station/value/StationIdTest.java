package wooteco.subway.domain.station.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.line.NegativeIdException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationIdTest {

    @DisplayName("stationId는 음수일 수 없다.")
    @Test
    void stationId() {
        assertThatThrownBy(() -> new StationId(-1L))
                .isInstanceOf(NegativeIdException.class)
                .hasMessage("id값은 음수일 수 없습니다.");
    }

    @Test
    void empty() {
        assertThat(StationId.empty().intValue()).isEqualTo(-1);
    }

    @Test
    void intValue() {
        assertThat(new StationId(0L).intValue()).isEqualTo(0);
    }

    @Test
    void longValue() {
        assertThat(new StationId(0L).longValue()).isEqualTo(0L);
    }

    @Test
    void floatValue() {
        assertThat(new StationId(0L).floatValue()).isEqualTo(0F);
    }

    @Test
    void doubleValue() {
        assertThat(new StationId(0L).doubleValue()).isEqualTo(0D);
    }

}