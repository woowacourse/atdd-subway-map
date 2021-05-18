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
import wooteco.subway.station.Station;

class SectionsTest {

    private final Station station1 = new Station("강남역");
    private final Station station2 = new Station("수서역");
    private final Station station3 = new Station("일원역");
    private final Station station4 = new Station("도곡역");
    private final Station station5 = new Station("개포역");
    private final Station station6 = new Station("잠실역");
    private final Section section1 = new Section(1L, station1, station2, 1);
    private final Section section2 = new Section(1L, station2, station4, 2);
    private final Section section3 = new Section(1L, station4, station5, 1);
    private final List<Section> sectionList = Arrays.asList(section1, section2, section3);

    @DisplayName("추가되는 구간의 역 중 하나만 노선에 포함되어 있어야 한다.")
    @Test
    public void validateStationsOfSection() {
        Section newSection = new Section(1L, station3, station6, 3);
        assertThatThrownBy(() -> new Sections(sectionList, newSection))
            .isInstanceOf(NoneOrAllStationsExistingInLineException.class);
    }

    @DisplayName("구간이 중간에 추가될 시 기존 구간보다 거리가 짧아야 한다.")
    @Test
    public void validateDistance() {
        Section newSection = new Section(1L, station2, station3, 3);
        assertThatThrownBy(() -> new Sections(sectionList, newSection))
            .isInstanceOf(InvalidSectionDistanceException.class);
    }

    @DisplayName("구간을 순서에 맞게 정렬한다.")
    @Test
    public void sort() {
        Sections sections = new Sections(sectionList);
        List<Station> actual = new LinkedList<>(sections.sortedStations());
        List<Station> expected = new LinkedList<>(
            Arrays.asList(station1, station2, station4, station5));
        assertThat(actual).isEqualTo(expected);
    }
}