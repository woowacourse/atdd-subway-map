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
    @DisplayName("생성 시 distance 가 0 이하인 경우 예외가 발생한다.")
    void createExceptionByNotPositiveDistance(final int distance) {
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");

        assertThatThrownBy(() -> new Section(1L, upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 길이는 양수만 들어올 수 있습니다.");
    }

    @Test
    @DisplayName("upStation 과 downStation 이 중복될 경우 예외가 발생한다.")
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
                () -> assertThat(downSection.isUpperSection(upSection)).isTrue(),
                () -> assertThat(upSection.isUpperSection(downSection)).isFalse()
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
                () -> assertThat(upSection.isLowerSection(downSection)).isTrue(),
                () -> assertThat(downSection.isLowerSection(upSection)).isFalse()
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

        assertThat(section.isConnectedSection(compareSection)).isTrue();
    }

    @Test
    @DisplayName("입력된 station을 포함하는지 확인할 수 있다.")
    void containsStation() {
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");

        Section section = new Section(1L, station1, station2, 2);

        assertAll(
                () -> assertThat(section.containsStation(station1)).isTrue(),
                () -> assertThat(section.containsStation(station3)).isFalse()
        );
    }

    @Test
    @DisplayName("입력된 station이 upStation과 같은지 확인할 수 있다.")
    void isUpStation() {
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");
        Section section = new Section(1L, upStation, downStation, 2);

        assertAll(
                () -> assertThat(section.isUpStation(upStation)).isTrue(),
                () -> assertThat(section.isUpStation(downStation)).isFalse()
        );
    }

    @Test
    @DisplayName("입력된 station이 downStation과 같은지 확인할 수 있다.")
    void isDownStation() {
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");
        Section section = new Section(1L, upStation, downStation, 2);

        assertAll(
                () -> assertThat(section.isDownStation(downStation)).isTrue(),
                () -> assertThat(section.isDownStation(upStation)).isFalse()
        );
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

    @Test
    @DisplayName("가운데 있는 Section으로 하행 새로운 Section을 만들어 반환할 수 있다.")
    void createMiddleSectionByDownStationSection() {
        // given
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");
        Section section = new Section(1L, 1L, station1, station3, 10);
        Section middleSection = new Section(2L, 1L, station1, station2, 3);

        // when
        Section updatedSection = section.createMiddleSectionByDownStationSection(middleSection);

        // then
        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, station2, station3, 7));
    }

    @Test
    @DisplayName("가운데 있는 Section으로 상행 새로운 Section을 만들어 반환할 수 있다.")
    void createMiddleSectionByUpStationSection() {
        // given
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");
        Section section = new Section(1L, 1L, station1, station3, 10);
        Section middleSection = new Section(2L, 1L, station2, station3, 3);

        // when
        Section updatedSection = section.createMiddleSectionByUpStationSection(middleSection);

        // then
        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, station1, station2, 7));
    }

    @Test
    @DisplayName("연장된 Section을 만들어 반환할 수 있다.")
    void createExtensionSection() {
        // given
        Station station1 = new Station(1L, "오리");
        Station station2 = new Station(2L, "배카라");
        Station station3 = new Station(3L, "오카라");
        Section section = new Section(1L, 1L, station1, station2, 10);
        Section middleSection = new Section(2L, 1L, station2, station3, 3);

        // when
        Section updatedSection = section.createExtensionSection(middleSection);

        // then
        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, station1, station3, 13));
    }
}
