package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("두 종점간의 거리가 0이하일 때 예외를 발생한다.")
    void invalidateDistance(final int distance) {
        final Station upStation = new Station("신대방역");
        final Station downStation = new Station("잠실역");

        assertThatThrownBy(() -> new Section(1L, 1L, upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("두 종점이 같은 역일 때 예외를 발생한다.")
    void duplicateStations() {
        final Station upStation = new Station("신대방역");
        final Station downStation = new Station("신대방역");

        assertThatThrownBy(() -> new Section(1L, 1L, upStation, downStation, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점은 같을 수 없습니다.");
    }

    @Test
    @DisplayName("구간 객체를 생성한다.")
    void createSection() {
        final long lineId = 1L;
        final Station upStation = new Station("신대방역");
        final Station downStation = new Station("잠실역");
        final int distance = 1;

        final Section section = new Section(1L, lineId, upStation, downStation, distance);

        assertAll(() -> {
            assertThat(section.getLineId()).isEqualTo(lineId);
            assertThat(section.getUpStation().isSameStation(upStation)).isTrue();
            assertThat(section.getDownStation().isSameStation(downStation)).isTrue();
            assertThat(section.getDistance()).isEqualTo(distance);
        });
    }
}
