package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.line.SortedStationIds;
import wooteco.subway.domain.section.Section;

public class SortedStationIdsTest {

    @Test
    void sortTest() {
        SortedStationIds sortedStationIds = new SortedStationIds(inputSections());
        assertThat(sortedStationIds.get()).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    private List<Section> inputSections() {
        int x = 1;
        List<Section> sections = Arrays.asList(
                new Section(7L, 8L, x),
                new Section(2L, 3L, x),
                new Section(5L, 6L, x),
                new Section(3L, 4L, x),
                new Section(8L, 9L, x),
                new Section(4L, 5L, x),
                new Section(1L, 2L, x),
                new Section(6L, 7L, x)
        );
        Collections.shuffle(sections);
        return sections;
    }
}
