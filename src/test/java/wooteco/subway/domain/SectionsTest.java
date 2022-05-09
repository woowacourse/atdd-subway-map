package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("section 리스트에서 station id를 추출한다")
    @Test
    void extractStationIds() {
        Section section1 = Section.of(1L, 1L, 2L, 3);
        Section section2 = Section.of(1L, 2L, 3L, 3);
        Section section3 = Section.of(1L, 4L, 5L, 3);

        Sections sections = new Sections(List.of(section1, section2, section3));
        Set<Long> stationIds = sections.extractStationIds();

        assertThat(stationIds.size()).isEqualTo(5);
        assertThat(stationIds.contains(1L)).isTrue();
        assertThat(stationIds.contains(2L)).isTrue();
        assertThat(stationIds.contains(3L)).isTrue();
        assertThat(stationIds.contains(4L)).isTrue();
        assertThat(stationIds.contains(5L)).isTrue();
    }
}
