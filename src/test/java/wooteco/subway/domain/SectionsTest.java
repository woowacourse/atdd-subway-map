package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

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
                .isInstanceOf(BusinessException.class)
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
                .isInstanceOf(BusinessException.class)
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
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("해당 구간의 거리가 추가하려는 구간보다 더 짧습니다.");
    }

    @Test
    @DisplayName("구간정보가 하나 뿐이라면 삭제 불가능 예외 발생")
    void validateDelete() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5)
        ));

        assertThatThrownBy(() -> sections.validateDelete())
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("하나의 노선에는 최소 하나의 구간이 필요합니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 구간에 존재하지 않다면 예외 발생")
    void notExistStation() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(1L, 1L, 2L, 3L, 5)
        ));

        assertThatThrownBy(() -> sections.findSectionByStationId(5L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("해당 노선에 존재하는 역이 아닙니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 담긴 구간 반환 - 종점인 경우")
    void findTerminusByStationId() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 5),
                new Section(2L, 1L, 2L, 3L, 5)
        ));

        List<Section> result = sections.findSectionByStationId(1L);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUpStationId()).isEqualTo(1L);
        assertThat(result.get(0).getDownStationId()).isEqualTo(2L);
        assertThat(result.get(0).getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("삭제하려는 역이 담긴 구간 반환 - 종점이 아닌 경우")
    void findSectionByStationId() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 3L, 4L, 4),
                new Section(3L, 1L, 2L, 3L, 5)
        ));

        List<Section> result = sections.findSectionByStationId(3L);

        List<Long> ids = result.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        List<Long> upStationIds = result.stream()
                .map(s -> s.getUpStationId())
                .collect(Collectors.toList());

        List<Long> downStationIds = result.stream()
                .map(s -> s.getDownStationId())
                .collect(Collectors.toList());

        List<Integer> distances = result.stream()
                .map(s -> s.getDistance())
                .collect(Collectors.toList());

        assertThat(result.size()).isEqualTo(2);
        assertThat(ids).containsOnly(2L, 3L);
        assertThat(upStationIds).containsOnly(2L, 3L);
        assertThat(downStationIds).containsOnly(3L, 4L);
        assertThat(distances).containsOnly(4, 5);
    }
}
