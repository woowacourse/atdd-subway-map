package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {
    @DisplayName("구간 내부에 구간 삽입 시(상행역이 같은 경우) 구간이 정상적으로 나뉘는지 확인한다,")
    @Test
    void changeDownStationAndDistance() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(1L, "강남역"), new Station(3L, "잠실역"), 7);

        section1.changeDownStationAndDistance(section2);
        assertThat(section1.getUpStation()).isEqualTo(section2.getDownStation());
        assertThat(section1.getDistance()).isEqualTo(3);
    }

    @DisplayName("구간 내부에 구간 삽입 시(하행역이 같은 경우) 구간이 정상적으로 나뉘는지 확인한다_2")
    @Test
    void changeUpStationAndDistance() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(3L, "잠실역"), new Station(2L, "선릉역"), 7);

        section1.changeUpStationAndDistance(section2);
        assertThat(section1.getDownStation()).isEqualTo(section2.getUpStation());
        assertThat(section1.getDistance()).isEqualTo(3);
    }
}
