package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EstimateTest {

    @DisplayName("추가할 구간과 삭제할 구간이 같으면 같은 객체로 취급한다.")
    @Test
    void equals() {
        List<Section> sectionsToCreate = Arrays
            .asList(new Section(1L, new Station(1L, "a"), new Station(2L, "b"), new Distance(1)),
                new Section(2L, new Station(5L, "e"), new Station(6L, "f"), new Distance(1)));
        List<Section> sectionsToCreate2 = Arrays
            .asList(new Section(1L, new Station(1L, "a"), new Station(2L, "b"), new Distance(1)),
                new Section(2L, new Station(5L, "e"), new Station(6L, "f"), new Distance(1)));
        List<Section> sectionsToDelete = Collections
            .singletonList(new Section(3L, new Station(3L, "c"), new Station(4L, "d"), new Distance(1)));
        List<Section> sectionsToDelete2 = Collections
            .singletonList(new Section(3L, new Station(3L, "c"), new Station(4L, "d"), new Distance(1)));
        Estimate estimate = new Estimate(sectionsToCreate, sectionsToDelete);
        Estimate estimate2 = new Estimate(sectionsToCreate2, sectionsToDelete2);
        assertThat(estimate).isEqualTo(estimate2);
    }
}
