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
}
