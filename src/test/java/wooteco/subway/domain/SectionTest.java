package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("상행역과 하행역이 같은 경우 에러를 반환한다.")
    @Test
    void validateUpAndDownAReDiffernt() {
        Station station = new Station(1L, "강남역");
        assertThatThrownBy(() -> new Section(station, station, 10)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("구간의 상행역과 하행역은 달라야합니다.");
    }

    @DisplayName("구간의 거리가 1보다 작으면 에러를 반환한다.")
    @Test
    void validateDistanceIsPositive() {
        Station up = new Station(1L, "강남역");
        Station down = new Station(2L, "선릉역");

        assertThatThrownBy(() -> new Section(up, down, 0)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("구간 사이의 거리는 양수여야합니다.");
    }

    @DisplayName("구간을 이어붙인다.")
    @Test
    void concatenate() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(2L, "선릉역"), new Station(3L, "역삼역"), 10);
        section1.concatenate(section2);

        assertThat(section1.getDownStation()).isEqualTo(new Station(3L, "역삼역"));
        assertThat(section1.getDistance()).isEqualTo(20);
    }

    @DisplayName("구간을 이어붙일 수 없는 경우 에러를 반환한다.")
    @Test
    void validateCanConcatenate() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(3L, "역삼역"), new Station(4L, "잠실역"), 10);
        assertThatThrownBy(() -> section1.concatenate(section2)).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("연결 조건이 맞지 않습니다.");
    }

    @DisplayName("구간 내부에 구간 삽입 시(상행역이 같은 경우) 구간이 정상적으로 나뉘는지 확인한다,")
    @Test
    void changeDownStationAndDistance() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(1L, "강남역"), new Station(3L, "잠실역"), 7);

        section1.changeStationAndDistance(section2);
        assertThat(section1.getUpStation()).isEqualTo(section2.getDownStation());
        assertThat(section1.getDistance()).isEqualTo(3);
    }

    @DisplayName("구간 내부에 구간 삽입 시(하행역이 같은 경우) 구간이 정상적으로 나뉘는지 확인한다_2")
    @Test
    void changeUpStationAndDistance() {
        Section section1 = new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10);
        Section section2 = new Section(new Station(3L, "잠실역"), new Station(2L, "선릉역"), 7);

        section1.changeStationAndDistance(section2);
        assertThat(section1.getDownStation()).isEqualTo(section2.getUpStation());
        assertThat(section1.getDistance()).isEqualTo(3);
    }
}
