package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidSectionDistanceException;
import wooteco.subway.exception.NoneOrAllStationsExistingInLineException;

class SectionsTest {

    private final Section section1 = new Section(1L, 1L, 2L, 1);
    private final Section section2 = new Section(1L, 4L, 5L, 1);
    private final Section section3 = new Section(1L, 2L, 4L, 2);
    private final Sections sections = new Sections(Arrays.asList(section1, section2, section3));

    @DisplayName("추가되는 구간의 역 중 하나만 노선에 포함되어 있어야 한다.")
    @Test
    public void validateStationsOfSection(){
        Section newSection = new Section(1L, 7L, 6L, 3);
        assertThatThrownBy(() -> sections.validateSectionStations(newSection))
            .isInstanceOf(NoneOrAllStationsExistingInLineException.class);
    }

    @DisplayName("구간이 중간에 추가될 시 기존 구간보다 거리가 짧아야 한다.")
    @Test
    public void validateDistance(){
        Section newSection = new Section(1L, 2L, 3L, 3);
        assertThatThrownBy(() -> sections.validateSectionDistance(newSection))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("구간을 순서에 맞게 정렬한다.")
    @Test
    public void sort(){
        List<Long> sortedStationsIds = new LinkedList<>(sections.sortedStationIds());
        List<Long> expectedIds = new LinkedList<>(Arrays.asList(1L, 2L, 4L, 5L));
        assertThat(sortedStationsIds).isEqualTo(expectedIds);
    }
}