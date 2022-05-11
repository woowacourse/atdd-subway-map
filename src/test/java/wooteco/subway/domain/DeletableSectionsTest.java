package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ExceptionMessage;

class DeletableSectionsTest {

    @Test
    @DisplayName("특정 역에 근접한 구간이 2개 이상이면 예외를 반환")
    void createNearSections() {
        // given
        List<Section> sections = List.of(new Section(1L, 2L, 3L, 4),
                new Section(1L, 3L, 4L, 5),
                new Section(1L, 4L, 5L, 6));

        // then
        assertThatThrownBy(() -> new DeletableSections(sections))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.NEAR_SECTIONS_OVER_SIZE.getContent());
    }

    @Test
    @DisplayName("삭제될 구간이 2개이면 구간을 합쳐서 반환")
    void mergeSections() {
        // given
        List<Section> sections = List.of(new Section(1L, 2L, 3L, 4),
                new Section(1L, 3L, 4L, 5));

        // when
        DeletableSections deletableSections = new DeletableSections(sections);
        Optional<Section> merged = deletableSections.mergeSections();

        // then
        assertAll(() -> {
            assertThat(merged.isPresent()).isTrue();
            assertThat(merged.get().getUpStationId()).isEqualTo(2L);
            assertThat(merged.get().getDownStationId()).isEqualTo(4L);
            assertThat(merged.get().getDistance()).isEqualTo(9);
        });
    }
}
