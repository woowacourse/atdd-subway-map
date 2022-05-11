package wooteco.subway.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("지하철역을 반환한다.")
    @Test
    void getStations() {
        Sections sections = new Sections(List.of(
                new Section(1L, 2L, 1L, 1),
                new Section(2L, 3L, 1L, 1),
                new Section(3L, 4L, 1L, 1)
        ));

        Assertions.assertThat(sections.getStationsId()).containsExactly(1L, 2L, 3L, 4L);
    }
}
