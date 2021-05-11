package wooteco.subway.domain.line.value.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @DisplayName("distance는 0보다 커야 한다")
    @Test
    void distance() {
        assertThatThrownBy(() -> new Distance(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0이나 음수일 수 없습니다.");

        assertThatThrownBy(() -> new Distance(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0이나 음수일 수 없습니다.");

        assertThat(new Distance(1L).intValue()).isEqualTo(1L);
    }

    @Test
    void intValue() {
        assertThat(new Distance(1L).intValue()).isEqualTo(1);
    }

    @Test
    void longValue() {
        assertThat(new Distance(1L).longValue()).isEqualTo(1L);
    }

    @Test
    void floatValue() {
        assertThat(new Distance(1L).floatValue()).isEqualTo(1F);
    }

    @Test
    void doubleValue() {
        assertThat(new Distance(1L).doubleValue()).isEqualTo(1D);
    }

}