package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    void sort() {
        final List<Long> expected = Arrays.asList(3L, 2L, 1L, 4L, 5L);
        final List<Section> sectionGroup = new ArrayList<>();
        sectionGroup.add(new Section(1L, 3L, 2L, 10));
        sectionGroup.add(new Section(1L, 4L, 5L, 5));
        sectionGroup.add(new Section(1L, 2L, 1L, 6));
        sectionGroup.add(new Section(1L, 1L, 4L, 8));

        final Sections sections = new Sections(sectionGroup);
        final List<Long> ids = sections.distinctStationIds();
        assertThat(ids).isEqualTo(expected);
    }
}