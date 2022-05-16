package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StationTest {
    @Test
    @DisplayName("역 id가 같으면 역이 서로 같다고 판단한다.")
    void isSameStation() {
        // given
        final Station station1 = new Station(1L, "역이름");
        final Station station2 = new Station(1L, "역이름");

        // when
        final boolean actual = station1.equals(station2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("역 id가 다르면 역이 서로 다르다고 판단한다.")
    void isDifferentStation() {
        // given
        final Station station1 = new Station(1L, "역이름");
        final Station station2 = new Station(2L, "역이름");

        // when
        final boolean actual = station1.equals(station2);

        // then
        assertThat(actual).isFalse();
    }

}
