package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SectionTest {
    @Test
    @DisplayName("구간을 라인 아이디, 상행역, 하행역, 거리를 가지고 생성할 수 있다.")
    void create() {
        // given & when
        Section section = new Section(1L, new Station(1L, "강남역"), new Station(2L, "역삼역"), 10);

        // then
        assertThat(section).isInstanceOf(Section.class);
    }

    @ParameterizedTest
    @MethodSource("invalidSectionCreate")
    @DisplayName("구간에 생성의 인자값으로 null값이 들어간 경우 예외가 발생한다.")
    void invalidSectionToParameterOfNull(Long lineId, Station upStation, Station downStation, int distance) {
        assertThatThrownBy(() -> new Section(lineId, upStation, downStation, distance))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> invalidSectionCreate() {
        return Stream.of(
                Arguments.of(null, new Station(1L, "강남역"), new Station(2L, "역삼역"), 1),
                Arguments.of(1L, null, new Station(2L, "역삼역"), 1),
                Arguments.of(1L, new Station(1L, "강남역"), null, 1)
        );
    }

    @Test
    @DisplayName("구간 생성시 거리가 0이하일 경우 예외가 발생한다.")
    void invalidSectionOfLessThanZeroDistance() {
        assertThatThrownBy(() -> new Section(1L, new Station(1L, "강남역"), new Station(2L, "역삼역"), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 거리는 0이하로 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("선행구간과 하행구간을 통해 하나의 구간으로 합병할 수 있다.")
    void mergerToSectionAndSection() {
        // given
        Section 선행구간 = new Section(1L, new Station(1L, "강남역"), new Station(2L, "역삼역"), 10);
        Section 하행구간 = new Section(1L, new Station(2L, "역삼역"), new Station(3L, "선릉역"), 10);

        // when
        final Section 합병구간 = 선행구간.merge(하행구간);

        // then
        assertThat(합병구간)
                .extracting("upStation", "downStation", "distance")
                .containsExactly(new Station(1L, "강남역"), new Station(3L, "선릉역"), 20);
    }
}
