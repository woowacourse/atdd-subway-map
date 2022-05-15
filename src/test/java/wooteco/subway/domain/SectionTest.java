package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("hasSameStation 메소드는 두 구간이 하나 이상의 같은 역을 포함하고 있다면 true를 반환한다.")
    @CsvSource(value = {"1,2,1,2", "1,2,3,2", "1,2,2,3"})
    @ParameterizedTest
    void hasSameStation(Long stationId1, Long stationId2, Long stationId3, Long stationId4) {
        // given
        Section section1 = new Section(stationId1, stationId2, new Distance(10));
        Section section2 = new Section(stationId3, stationId4, new Distance(10));

        // when
        boolean actual = section1.hasSameStation(section2);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("hasSameStation 메소드는 두 구간이 하나도 같은 역이 없다면 false를 반환한다.")
    @CsvSource(value = {"1,2,4,5", "1,2,7,10", "1,2,6,3"})
    @ParameterizedTest
    void hasSameStation_returnsFalse(Long stationId1, Long stationId2, Long stationId3, Long stationId4) {
        // given
        Section section1 = new Section(stationId1, stationId2, new Distance(10));
        Section section2 = new Section(stationId3, stationId4, new Distance(10));

        // when
        boolean actual = section1.hasSameStation(section2);

        // then
        assertThat(actual).isFalse();
    }
}
