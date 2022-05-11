package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        boolean result = sections.isTerminus(new Section(1L, 1L, 1L, 2L, 5));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("하행 종점이라면 true 반환")
    void isTerminusWhenDown() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        boolean result = sections.isTerminus(new Section(1L, 1L, 3L, 4L, 5));

        assertThat(result).isTrue();
    }


    @Test
    @DisplayName("종점이 아니라면 false 반환")
    void isTerminusWhenFalse() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        boolean result = sections.isTerminus(new Section(1L, 1L, 5L, 6L, 5));

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @CsvSource({"1,4", "4,2"})
    @DisplayName("새로운 구간이 추가 될 기존 구간 반환")
    void findSource(Long up, Long down) {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        Section target = new Section(2L, 1L, up, down, 2);

        Section source = sections.findSource(target);

        assertThat(source.getUpStationId()).isEqualTo(1L);
        assertThat(source.getDownStationId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("이미 존재하는 구간이라면 예외 발생")
    void alreadyAdded() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        Section target = new Section(1L, 1L, 2L, 3L, 5);

        assertThatThrownBy(() -> sections.validateTarget(target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록되어있는 구간입니다.");
    }

    @Test
    @DisplayName("추가할 수 있는 구간이 없다면 예외 발생")
    void noSection() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        Section target = new Section(1L, 1L, 7L, 8L, 5);

        assertThatThrownBy(() -> sections.validateTarget(target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("추가할 수 있는 구간이 없습니다.");
    }

    @Test
    @DisplayName("추가할 수 있는 구간 거리가 원래 구간보다 크다면 예외 발생")
    void tooLongDistance() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 3L, 4L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));
        Section target = new Section(1L, 1L, 2L, 5L, 5);

        assertThatThrownBy(() -> sections.findSource(target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 구간의 거리가 추가하려는 구간보다 더 짧습니다.");
    }
}
