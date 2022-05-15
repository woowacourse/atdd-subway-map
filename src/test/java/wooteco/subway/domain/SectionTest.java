package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class SectionTest {

    private final Section section = new Section(1L, 1L, 2L, 3L, 5);

    @DisplayName("다른 구간의 구간 길이보다 자신의 구간 길이가 크거나 같은지 반환한다.")
    @ParameterizedTest
    @CsvSource({"5, true", "6, false"})
    void hasHigherDistance(int distance, boolean expected) {
        Section another = new Section(2L, 1L, 5L, 6L, distance);

        boolean actual = section.hasHigherDistance(another);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("다른 구간을 받아, 자신의 역 중에 다른 구간의 상행선과 같은 역이 있는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"3, true", "4, false"})
    void hasSameUpStationOf(Long upStationId, boolean expected) {
        Section another = new Section(2L, 1L, upStationId, 5L, 4);

        boolean actual = section.hasSameUpStationOf(another);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("다른 구간을 받아, 자신의 역 중에 다른 구간의 하행선과 같은 역이 있는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"3, true", "4, false"})
    void hasSameDownStationOf(Long downStationId, boolean expected) {
        Section another = new Section(2L, 1L, 5L, downStationId, 4);

        boolean actual = section.hasSameDownStationOf(another);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("다른 구간을 받아, 자신의 상행선과 다른 구간의 상행선이 같은지 반환한다.")
    @ParameterizedTest
    @CsvSource({"2, true", "3, false"})
    void hasSameUpStation(Long upStationId, boolean expected) {
        Section another = new Section(2L, 1L, upStationId, 5L, 4);

        boolean actual = section.hasSameUpStation(another);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("다른 구간을 받아, 자신의 하행선과 다른 구간의 하행선이 같은지 반환한다.")
    @ParameterizedTest
    @CsvSource({"3, true", "4, false"})
    void hasSameDownStation(Long downStationId, boolean expected) {
        Section another = new Section(2L, 1L, 5L, downStationId, 4);

        boolean actual = section.hasSameDownStation(another);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("자신의 구간 사이에 새로운 구간이 들어와서 바뀌게 될 구간을 반환한다.")
    @ParameterizedTest
    @MethodSource("provideAddedSectionAndExpected")
    void change(Section added, Section expected) {
        Section actual = this.section.changeSection(added);

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideAddedSectionAndExpected() {
        return Stream.of(
                Arguments.of(new Section(1L, 1L, 2L, 4L, 3),
                        new Section(1L, 1L, 4L, 3L, 2)),
                Arguments.of(new Section(1L, 1L, 4L, 3L, 3),
                        new Section(1L, 1L, 2L, 4L, 2)));
    }

    @DisplayName("자신을 포함한 구간들을 받아 구간들 중에 자신이 상행선인지 반환한다.")
    @Test
    void isUpSection() {
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 3);
        Section third = new Section(1L, 3L, 4L, 3);
        List<Section> sections = Arrays.asList(third, first, second);

        assertAll(
                () -> assertThat(first.isUpSection(sections)).isTrue(),
                () -> assertThat(second.isUpSection(sections)).isFalse(),
                () -> assertThat(third.isUpSection(sections)).isFalse()
        );
    }

    @DisplayName("다른 구간들을 받아 자신의 다음으로 이어지는 구간을 반환한다.")
    @Test
    void findNextSection() {
        Section first = new Section(1L, 1L, 2L, 3);
        Section second = new Section(1L, 2L, 3L, 3);
        Section third = new Section(1L, 3L, 4L, 3);
        List<Section> sections = Arrays.asList(third, first, second);

        assertAll(
                () -> assertThat(first.findNextSection(sections)).isEqualTo(second),
                () -> assertThat(second.findNextSection(sections)).isEqualTo(third),
                () -> assertThatThrownBy(() -> third.findNextSection(sections))
                        .isInstanceOf(IllegalStateException.class)
        );
    }

    @DisplayName("지하철 역 id를 받아 해당 id를 가지고 있는지 반환한다.")
    @ParameterizedTest
    @CsvSource({"1, false", "2, true", "3, true"})
    void hasStationId(Long stationId, boolean expected) {
        boolean actual = section.hasStationId(stationId);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("다른 구간을 받아 자신과 같은 역을 기준으로 결합한 구간을 반환한다.")
    @Test
    void combine() {
        Section another = new Section(1L, 1L, 3L, 4L, 5);

        Section combined = section.combine(another);

        assertThat(combined).isEqualTo(new Section(1L, 1L, 2L, 4L, 10));
    }
}
