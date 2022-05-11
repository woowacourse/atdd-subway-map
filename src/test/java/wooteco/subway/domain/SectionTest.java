package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("거리가 1 미만인 구간은 생성할 수 없다.")
    @Test
    void cannotCreateDistanceLessThanOneSection() {
        Station upStation = new Station(1L, "서울역");
        Station downStation = new Station(2L, "시청");
        int distance = 0;

        assertThatThrownBy(() -> new Section(1L, upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리가 1 미만");
    }

    @DisplayName("특정 역을 상행 혹은 하행으로 등록하고 있는지 확인한다.")
    @Test
    void contains() {
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Station station3 = new Station(3L, "station3");
        Section section = new Section(1L, station1, station2, 10);

        assertAll(
                () -> assertThat(section.contains(station1)).isTrue(),
                () -> assertThat(section.contains(station2)).isTrue(),
                () -> assertThat(section.contains(station3)).isFalse()
        );
    }

    @DisplayName("입력된 구간이 같은 역을 가지고 있는지 확인한다.")
    @Test
    void containsSameStations() {
        Station station1 = new Station(1L, "station1");
        Station station2 = new Station(2L, "station2");
        Station station3 = new Station(3L, "station3");


        Section section1 = new Section(1L, station1, station2, 10);
        Section section2 = new Section(2L, station1, station2, 9);
        Section section3 = new Section(3L, station2, station1, 8);
        Section section4 = new Section(4L, station2, station3, 7);

        assertAll(
                () -> assertThat(section1.containsSameStations(section2)).isTrue(),
                () -> assertThat(section1.containsSameStations(section3)).isTrue(),
                () -> assertThat(section1.containsSameStations(section4)).isFalse()
        );
    }
}
