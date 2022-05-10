package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SectionTest {

    @Test
    @DisplayName("상행, 하행 역이 같은 경우 예외를 발생시킨다.")
    void exceptionSameStationId() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행, 하행 역은 서로 달라야합니다.");
    }

    @ParameterizedTest(name = "[{index}] 거리 : {0} ")
    @ValueSource(ints = {-1, 0})
    @DisplayName("구간의 거리가 0 이하인 경우 예외를 발생시킨다.")
    void exceptionIllegalDistance(final int distance) {
        assertThatThrownBy(() -> new Section(1L, 1L, 2L, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 거리는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("같은 상행 역 ID를 가지는 지 확인한다. - 참")
    void hasSameUpStationId_true() {
        // givne
        final Section section1 = new Section(1L, 1L, 3L, 10);
        final Section section2 = new Section(1L, 1L, 2L, 10);

        // when
        final Boolean actual = section1.hasSameUpStationId(section2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("같은 상행 역 ID를 가지는 지 확인한다. - 거짓")
    void hasSameUpStationId_false() {
        // givne
        final Section section1 = new Section(1L, 1L, 3L, 10);
        final Section section2 = new Section(1L, 3L, 2L, 10);

        // when
        final Boolean actual = section1.hasSameUpStationId(section2);

        // then
        assertThat(actual).isFalse();
    }
}
