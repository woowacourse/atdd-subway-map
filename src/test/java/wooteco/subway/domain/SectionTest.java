package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    private final Section section = new Section(1L, 2L, 30);

    @Test
    @DisplayName("상행역과 하행역이 같을 경우 예외를 발생시킨다.")
    void sameUpAndDown() {
        assertThatThrownBy(() -> new Section(1L, 1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

    @Test
    @DisplayName("거리가 0 미만일 경우 예외를 발생시킨다.")
    void validateDistance() {
        assertThatThrownBy(()->new Section(1L, 3L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 거리는 0 이하일 수 없습니다.");
    }

    @ParameterizedTest
    @DisplayName("해당 구간에 특정 구간의 상행역이 존재하는지 알려준다.")
    @CsvSource({"1, 3, 5, true", "1, 2, 30, true",
            "3, 2, 25, false", "3, 5, 45, false"})
    void hasSameUpStation(Long upStationId, Long downStationId, int distance, boolean expected) {
        final Section given = new Section(upStationId, downStationId, distance);

        assertThat(section.hasSameUpStation(given)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("해당 구간에 특정 구간의 하행역이 존재하는지 알려준다.")
    @CsvSource({"1, 3, 5, false", "1, 2, 30, true",
            "3, 2, 25, true", "3, 5, 45, false"})
    void hasStation(Long upStationId, Long downStationId, int distance, boolean expected) {
        final Section given = new Section(upStationId, downStationId, distance);

        assertThat(section.hasSameDownStation(given)).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 구간과 같은 상행역을 가지는지 비교한다.")
    void saveSameUpStation() {
        final Section given = new Section(1L, 3L, 5);

        assertThat(section.hasSameUpStation(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 같은 하행역을 가지는지 비교한다.")
    void hasSameDownStation() {
        final Section given = new Section(3L, 2L, 25);

        assertThat(section.hasSameDownStation(given)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("특정 구간과의 길이를 비교한다.")
    @CsvSource({"1, 3, 5, true", "1, 2, 30, false", "1, 5, 50, false"})
    void isLongerThan(Long upStationId, Long downStationId, int distance, boolean expected) {
        final Section given = new Section(upStationId, downStationId, distance);

        assertThat(section.isLongerThan(given)).isEqualTo(expected);
    }

    @Test
    @DisplayName("상행역이 특정 구간의 하행역과 같은지 비교한다.")
    void hasSameUpStationWithOtherDownStation() {
        final Section given = new Section(4L, 1L, 20);

        assertThat(section.hasSameUpStationWithOtherDownStation(given)).isTrue();
    }

    @Test
    @DisplayName("하행역이 특정 구간의 상행역과 같은지 비교한다.")
    void hasSameDownStationWithOtherUpStation() {
        final Section given = new Section(2L, 5L, 20);

        assertThat(section.hasSameDownStationWithOtherUpStation(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 완전히 동일한 역을 가지는지 비교한다.")
    void isSameStations() {
        final Section given = new Section(1L, 2L, 30);

        assertThat(section.isSameStations(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 겹치는 역이 하나도 없는지 비교한다.")
    void isNotSameAnyStation() {
        final Section given = new Section(3L, 6L, 40);

        assertThat(section.isNotSameAnyStation(given)).isTrue();
    }
}
