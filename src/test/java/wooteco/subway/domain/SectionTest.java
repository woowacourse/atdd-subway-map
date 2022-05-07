package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("생성 시 distance 가 0 이하인 경우 예외 발생")
    void createExceptionByNotPositiveDistance(final int distance) {
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");

        assertThatThrownBy(() -> new Section(1L, upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 길이는 양수만 들어올 수 있습니다.");
    }

    @Test
    @DisplayName("upStation 과 downStation 이 중복될 경우 예외 발생")
    void createExceptionDByDuplicateStationId() {
        Station station = new Station(1L, "오리");

        assertThatThrownBy(() -> new Section(1L, station, station, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("upstation과 downstation은 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("입력된 section이 upSection인지 확인할 수 있다.")
    void isUpSection() {
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");

        Section upSection = new Section(1L, station1, station2, 2);
        Section downSection = new Section(1L, station2, station3, 3);

        assertAll(
                () -> assertThat(downSection.isUpSection(upSection)).isTrue(),
                () -> assertThat(upSection.isUpSection(downSection)).isFalse()
        );
    }

    @Test
    @DisplayName("입력된 section이 downSection인지 확인할 수 있다.")
    void isDownSection() {
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");

        Section upSection = new Section(1L, station1, station2, 2);
        Section downSection = new Section(1L, station2, station3, 3);

        assertAll(
                () -> assertThat(upSection.isDownSection(downSection)).isTrue(),
                () -> assertThat(downSection.isDownSection(upSection)).isFalse()
        );
    }

    @Test
    @DisplayName("입력된 section이 현재와 연결되어 상행 구간인지 하행 구간인지 확인할 수 있다.")
    void isUpSectionOrDownSection() {
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");

        Section section = new Section(1L, station1, station2, 2);
        Section compareSection = new Section(1L, station1, station3, 3);

        assertThat(section.isUpSectionOrDownSection(compareSection)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"4,false", "5,true", "6,true"})
    @DisplayName("입력된 section의 길이가 크거나 같은 지 확인할 수 있다.")
    void isEqualsOrLargerDistance(final int distance, final boolean expected) {
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");

        Section section = new Section(1L, station1, station2, 5);
        Section compareSection = new Section(1L, station1, station3, distance);

        assertThat(section.isEqualsOrLargerDistance(compareSection)).isEqualTo(expected);
    }
}
