package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("기존의 구간이 새로운 길이보다 작거나 같으면 true를 반환한다.")
    @Test
    void isEqualsAndSmallerThan() {
        Section section = new Section(1L, new Station("합정역"), new Station("홍대입구역"), 10);
        assertThat(section.isEqualsAndSmallerThan(11)).isTrue();
    }

    @DisplayName("상행역과 하행역이 서로 모두 같으면 true를 반환한다.")
    @Test
    void isSameUpDownStation() {
        Section section = new Section(1L, new Station(1L, "합정역"), new Station(2L, "홍대입구역"), 10);
        assertThat(section.isSameUpDownStation(1L, 2L)).isTrue();
    }

    @DisplayName("상행id와 하행id를 가지고 있다면 true를 반환한다.")
    @Test
    void haveStationId() {
        Section section = new Section(1L, new Station(1L, "합정역"), new Station(2L, "홍대입구역"), 10);
        assertThat(section.haveStationId(2L, 1L)).isTrue();
    }
}
