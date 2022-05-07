package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionTest {
    private final Section section = new Section(new Station("건대입구역"), new Station("잠실역"), 30);

    @Test
    @DisplayName("상행역과 하행역이 같을 경우 예외를 발생시킨다.")
    void sameUpAndDown() {
        assertThatThrownBy(() -> new Section(new Station("건대입구역"), new Station("건대입구역"), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

    @ParameterizedTest
    @DisplayName("해당 구간에 특정 구간의 상행역이 존재하는지 알려준다.")
    @CsvSource({"건대입구역, 구의역, 5, true", "건대입구역, 잠실역, 30, true",
            "구의역, 잠실역, 25, false", "구의역, 선릉역, 45, false"})
    void hasSameUpStation(String upStation, String downStation, int distance, boolean expected) {
        final Section given = new Section(new Station(upStation), new Station(downStation), distance);

        assertThat(section.hasSameUpStation(given)).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("해당 구간에 특정 구간의 하행역이 존재하는지 알려준다.")
    @CsvSource({"건대입구역, 구의역, 5, false", "건대입구역, 잠실역, 30, true",
            "구의역, 잠실역, 25, true", "구의역, 선릉역, 45, false"})
    void hasStation(String upStation, String downStation, int distance, boolean expected) {
        final Section given = new Section(new Station(upStation), new Station(downStation), distance);

        assertThat(section.hasSameDownStation(given)).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 구간과 같은 상행역을 가지는지 비교한다.")
    void saveSameUpStation() {
        final Section given = new Section(new Station("건대입구역"), new Station("구의역"), 5);

        assertThat(section.hasSameUpStation(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 같은 하행역을 가지는지 비교한다.")
    void hasSameDownStation() {
        final Section given = new Section(new Station("구의역"), new Station("잠실역"), 25);

        assertThat(section.hasSameDownStation(given)).isTrue();
    }

    @ParameterizedTest
    @DisplayName("특정 구간과의 길이를 비교한다.")
    @CsvSource({"건대입구역, 구의역, 5, true", "건대입구역, 잠실역, 30, false", "건대입구역, 선릉역, 50, false"})
    void isLongerThan(String upStation, String downStation, int distance, boolean expected) {
        final Section given = new Section(new Station(upStation), new Station(downStation), distance);

        assertThat(section.isLongerThan(given)).isEqualTo(expected);
    }

    @Test
    @DisplayName("상행역이 특정 구간의 하행역과 같은지 비교한다.")
    void hasSameUpStationWithOtherDownStation() {
        final Section given = new Section(new Station("한양대역"), new Station("건대입구역"), 20);

        assertThat(section.hasSameUpStationWithOtherDownStation(given)).isTrue();
    }

    @Test
    @DisplayName("하행역이 특정 구간의 상행역과 같은지 비교한다.")
    void hasSameDownStationWithOtherUpStation() {
        final Section given = new Section(new Station("잠실역"), new Station("선릉역"), 20);

        assertThat(section.hasSameDownStationWithOtherUpStation(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 완전히 동일한 역을 가지는지 비교한다.")
    void isSameStations() {
        final Section given = new Section(new Station("건대입구역"), new Station("잠실역"), 30);

        assertThat(section.isSameStations(given)).isTrue();
    }

    @Test
    @DisplayName("특정 구간과 겹치는 역이 하나도 없는지 비교한다.")
    void isNotSameAnyStation() {
        final Section given = new Section(new Station("구의역"), new Station("삼성역"), 40);

        assertThat(section.isNotSameAnyStation(given)).isTrue();
    }
}
