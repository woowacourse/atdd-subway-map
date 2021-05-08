package wooteco.subway.domain.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    private Section firstSection;
    private Section secondSection;
    private Section thirdSection;
    private Section fourthSection;

    @BeforeEach
    void setUp() {
        Station firstStation = new Station(1L, "천호역");
        Station secondStation = new Station(2L, "강남역");
        Station thirdStation = new Station(3L, "회기역");
        Station fourthStation = new Station(4L, "의정부역");
        Station fifthStation = new Station(5L, "태릉역");
        firstSection = new Section(firstStation, secondStation, 10, 1L);
        secondSection = new Section(secondStation, thirdStation, 10, 1L);
        thirdSection = new Section(thirdStation, fourthStation, 10, 1L);
        fourthSection = new Section(fourthStation, fifthStation, 10, 1L);

    }

    @DisplayName("정렬되지 않은 Section의 상행 및 하행역을 조회하여 정렬된 역 리스트를 반환한다.")
    @Test
    void sort() {
        List<Section> unsortedSections = Arrays.asList(thirdSection, firstSection, fourthSection, secondSection);

        Sections sections = new Sections(unsortedSections);

        assertThat(sections.toList()).containsExactly(firstSection, secondSection, thirdSection, fourthSection);
    }

    @DisplayName("Section들을 정렬하고 구간들에 포함된 중복이 없는 역을 순서대로 반환한다.")
    @Test
    void convertIntoStations() {
        List<Section> unsortedSections = Arrays.asList(secondSection, fourthSection, firstSection, thirdSection);

        Sections sections = new Sections(unsortedSections);
        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(new Station(1L, "천호역"),
                new Station(2L, "강남역"), new Station(3L, "회기역"),
                new Station(4L, "의정부역"), new Station(5L, "태릉역"));
    }
}
