package wooteco.subway.station.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.domain.Section;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StationsTest {
    private Stations stations;
    private Station firstStation = new Station(1L, "해운대역");
    private Station secondStation = new Station(2L, "몽촌토성역");
    private Station thirdStation = new Station(3L, "광안역");
    private Station fourthStation = new Station(4L, "잠실역");
    private Station fifthStation = new Station(5L, "잠실나루역");

    @BeforeEach
    void setUp() {
        stations = new Stations(
                Arrays.asList(firstStation, secondStation, thirdStation)
        );
    }

    @DisplayName("구간의 상행선과 하행선 모두 역들에 포함되면 true, 아니면 false")
    @Test
    void containsAll() {
        Section section = new Section(1L, thirdStation, secondStation, 10);
        assertThat(stations.containsAll(section)).isTrue();

        section = new Section(1L, fourthStation, secondStation, 10);
        assertThat(stations.containsAll(section)).isFalse();
    }

    @DisplayName("구간의 상행선과 하행선 모두 역들에 포함되어있지 않으면 true, 어느 하나라도 포함 되어있다면 false")
    @Test
    void containsNone() {
        Section section = new Section(1L, thirdStation, secondStation, 10);
        assertThat(stations.containsNone(section)).isFalse();

        section = new Section(1L, fourthStation, secondStation, 10);
        assertThat(stations.containsNone(section)).isFalse();

        section = new Section(1L, fourthStation, fifthStation, 10);
        assertThat(stations.containsNone(section)).isTrue();
    }

    @DisplayName("역들 중 주어진 이름을 가지고 있으면 true, 아니면 false")
    @Test
    void doesNameExist() {
        assertThat(stations.doesNameExist(firstStation.getName())).isTrue();
        assertThat(stations.doesNameExist(fifthStation.getName())).isFalse();
    }

    @DisplayName("역들 중 주어진 id를 가지고 있지 않으면 true, 가지고 있으면 false")
    @Test
    void doesIdNotExist() {
        assertThat(stations.doesIdNotExist(firstStation.getId())).isFalse();
        assertThat(stations.doesIdNotExist(fifthStation.getId())).isTrue();
    }
}