package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("sections를 중복없이 stationId 리스트로 변환하여 반환한다.")
    @Test
    void getStationIds() {
        List<Section> sections = List.of(
                new Section(1L, 1L, 2L, 5),
                new Section(1L, 3L, 5L, 5),
                new Section(1L, 2L, 3L, 5)

        );

        List<Long> actual = new Sections(sections).getStationIds();
        List<Long> expected = List.of(1L, 2L, 3L, 5L);

        assertThat(actual).isEqualTo(expected);
    }
}
