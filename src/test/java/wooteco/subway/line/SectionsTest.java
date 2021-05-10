package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Section section1 = new Section(1L, 1L, 2L, 3L, 3);
    private Section section2 = new Section(1L, 1L, 3L, 4L, 5);


    @Test
    @DisplayName("구간이 주어졌을 때 정렬된 역 id 리스트 생성")
    void sectionSorting() {
        Sections sections = new Sections(Arrays.asList(section1, section2));

        List<Long> actual = sections.stationRoute();
        assertThat(actual).containsExactly(2L,3L,4L);
    }
}