package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixtures.ID_1;
import static wooteco.subway.Fixtures.ID_2;
import static wooteco.subway.Fixtures.ID_3;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("구간들에서 역들을 추출한다.")
    void toStationIds() {
        final Sections sections = new Sections(List.of(new Section(1L, 1L, 2L, 10),
                new Section(1L, 3L, 4L, 10),
                new Section(1L, 2L, 3L, 10)));

        final List<Long> stationIds = sections.toSortedStationIds();

        assertThat(stationIds).containsExactly(1L, 2L, 3L, 4L);
    }
}
