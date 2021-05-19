package wooteco.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {
    private Section section;
    private Section newSection;
    private final Station firstStation = new Station(1L, "해운대역");
    private final Station secondStation = new Station(2L, "몽촌토성역");
    private final Station thirdStation = new Station(3L, "광안역");

    @BeforeEach
    void setUp() {
        section = new Section(1L, firstStation, secondStation, 10);
        newSection = new Section(1L, firstStation, thirdStation, 6);
    }

    @DisplayName("구간을 추가할 때, 기존 구간이 새로 추가되는 구간에 의해 조정된다")
    @Test
    void adjustBy() {
        // 1 - 3 - 2
        Section adjustedSection = section.adjustBy(newSection);
        Section expectedAdjustedSection = new Section(1L, new Station(thirdStation.getId()), new Station(secondStation.getId()), 4);

        assertThat(adjustedSection).usingRecursiveComparison().isEqualTo(expectedAdjustedSection);
    }

    @DisplayName("두 구간이 서로 같은 상행선을 가지면 true, 아니면 false")
    @Test
    void hasSameUpStation() {
        assertThat(section.hasSameUpStation(newSection)).isTrue();
    }

    @DisplayName("두 구간이 서로 같은 하행선을 가지면 true, 아니면 false")
    @Test
    void hasSameDownStation() {
        assertThat(section.hasSameDownStation(newSection)).isFalse();
    }

    @DisplayName("구간이 주어진 역을 상행선으로 가지면 true, 아니면 false")
    @Test
    void hasUpStation() {
        assertThat(section.hasUpStation(firstStation)).isTrue();
        assertThat(section.hasUpStation(secondStation)).isFalse();
    }

    @DisplayName("구간이 주어진 역을 하행선으로 가지면 true, 아니면 false")
    @Test
    void hasDownStation() {
        assertThat(section.hasDownStation(firstStation)).isFalse();
        assertThat(section.hasDownStation(secondStation)).isTrue();
    }

    @DisplayName("구간이 주어진 역을 상행선 하행선 둘 중 하나라도 가지면 true, 아니면 false")
    @Test
    void hasStation() {
        assertThat(section.hasStation(firstStation)).isTrue();
        assertThat(section.hasStation(secondStation)).isTrue();
        assertThat(section.hasStation(thirdStation)).isFalse();
    }
}