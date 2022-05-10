package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    
    @Test
    @DisplayName("역들의 id를 반환한다.")
    void getStations() {
        final Section first = new Section(1L, 1L, 2L, 3);
        final Section second = new Section(1L, 2L, 3L, 5);
        Sections sections = new Sections(Arrays.asList(first, second));
        assertThat(sections.getStationIds()).contains(1L, 2L, 3L);
    }
}