package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @Test
    @DisplayName("정렬된 Station id 반환")
    void getSortedStationIds() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 4L, 5L, 5),
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));

        List<Long> result = sections.getSortedStationIds();

        assertThat(result).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    @DisplayName("상행 종점이라면 true 반환")
    void isTerminusWhenUp() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        boolean result = sections.isTerminus(1L, 2L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("하행 종점이라면 true 반환")
    void isTerminusWhenDown() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        boolean result = sections.isTerminus(3L, 4L);

        assertThat(result).isTrue();
    }


    @Test
    @DisplayName("종점이 아니라면 false 반환")
    void isTerminusWhenFalse() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        boolean result = sections.isTerminus(5L, 6L);

        assertThat(result).isFalse();
    }
}
