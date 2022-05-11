package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @DisplayName("지하철 구간들(sections) 중에서 요청 상행, 하행역과 연관될 구간(section)을 정한다.")
    @Test
    void getSectionForCombine1() {
        List<Section> sections = List.of(
                new Section(1L, 1L, 2L, 5),
                new Section(1L, 2L, 3L, 5)

        );

        Section section = new Sections(sections).getSectionForCombine(2L, 4L);
        assertAll(
                () -> section.getUpStationId().equals(2L),
                () -> section.getUpStationId().equals(3L)
        );
    }

    @DisplayName("지하철 구간들(sections) 중에서 요청 상행, 하행역과 연관될 구간(section)을 정한다.")
    @Test
    void getSectionForCombine2() {
        List<Section> sections = List.of(
                new Section(1L, 1L, 2L, 5)
        );

        Section section = new Sections(sections).getSectionForCombine(2L, 4L);
        assertAll(
                () -> section.getUpStationId().equals(1L),
                () -> section.getUpStationId().equals(2L)
        );
    }
}
