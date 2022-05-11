package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("같은 상행역을 가지고 있는지 비교한다.")
    @Test
    void hasSameUpStation_true() {
        Section section1 = new Section(1L, 1L, 2L, 3L, 5);
        Section section2 = new Section(1L, 1L, 2L, 4L, 5);

        assertThat(section1.hasSameUpStation(section2)).isTrue();
    }

    @DisplayName("다른 상행역을 가지고 있는지 비교한다.")
    @Test
    void hasSameUpStation_false() {
        Section section1 = new Section(1L, 1L, 2L, 3L, 5);
        Section section2 = new Section(1L, 1L, 3L, 4L, 5);

        assertThat(section1.hasSameUpStation(section2)).isFalse();
    }

    @DisplayName("같은 하행역을 가지고 있는지 비교한다.")
    @Test
    void hasSameDownStation_true() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);
        Section section2 = new Section(1L, 1L, 2L, 3L, 5);

        assertThat(section1.hasSameDownStation(section2)).isTrue();
    }

    @DisplayName("다른 하행역을 가지고 있는지 비교한다.")
    @Test
    void hasSameDownStation_false() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);
        Section section2 = new Section(1L, 1L, 2L, 4L, 5);

        assertThat(section1.hasSameDownStation(section2)).isFalse();
    }

    @DisplayName("같은 상행역id를 가지고 있는지 비교한다.")
    @Test
    void isSameUpStationId_true() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);

        assertThat(section1.isSameUpStationId(1L)).isTrue();
    }

    @DisplayName("다른 상행역id를 가지고 있는지 비교한다.")
    @Test
    void isSameUpStationId_false() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);

        assertThat(section1.isSameUpStationId(2L)).isFalse();
    }

    @DisplayName("같은 하행역id를 가지고 있는지 비교한다.")
    @Test
    void isSameDownStationId_true() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);

        assertThat(section1.isSameDownStationId(3L)).isTrue();
    }

    @DisplayName("다른 하행역id를 가지고 있는지 비교한다.")
    @Test
    void isSameDownStationId_false() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);

        assertThat(section1.isSameDownStationId(2L)).isFalse();
    }

    @DisplayName("두 구간의 거리가 크거나 같은지 비교한다. - true")
    @Test
    void isGreaterOrEqualDistanceThan_true() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 5);
        Section section2 = new Section(1L, 1L, 2L, 3L, 5);

        assertThat(section1.isGreaterOrEqualDistanceThan(section2)).isTrue();
    }

    @DisplayName("두 구간의 거리가 크거나 같은지 비교한다. - false")
    @Test
    void isGreaterOrEqualDistanceThan_false() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 4);
        Section section2 = new Section(1L, 1L, 2L, 3L, 5);

        assertThat(section1.isGreaterOrEqualDistanceThan(section2)).isFalse();
    }

    @DisplayName("두 구간의 거리가 크거나 같은지 비교한다. - false")
    @Test
    void calculateDistanceDifference() {
        Section section1 = new Section(1L, 1L, 1L, 3L, 4);
        Section section2 = new Section(1L, 1L, 2L, 3L, 5);

        assertThat(section2.calculateDistanceDifference(section1)).isEqualTo(1);
    }
}