package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("정렬된 역 아이디를 반환한다.")
    void toStationIds() {
        // given
        final List<Section> shuffledSections = new ArrayList<>(List.of(
                new Section(1L, 1L, 1L, 2L, 1),
                new Section(2L, 1L, 2L, 3L, 1),
                new Section(3L, 1L, 3L, 4L, 1),
                new Section(4L, 1L, 4L, 5L, 1),
                new Section(5L, 1L, 5L, 6L, 1),
                new Section(6L, 1L, 6L, 7L, 1),
                new Section(7L, 1L, 7L, 8L, 1)
        ));
        Collections.shuffle(shuffledSections);
        final Sections sections = new Sections(shuffledSections);

        // when
        final List<Long> actual = sections.toStations();

        // then
        assertThat(actual).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
    }
}