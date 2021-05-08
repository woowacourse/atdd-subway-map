package wooteco.subway.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {

    @DisplayName("정렬되지 않은 Section의 상행 및 하행역을 조회하여 정렬된 역 리스트를 반환한다.")
    @Test
    void convertIntoStations() {
        Station firstStation = new Station("천호역");
        Station secondStation = new Station("강남역");
        Station thirdStation = new Station("회기역");
        Station fourthStation = new Station("의정부역");
        Station fifthStation = new Station("태릉역");

        Section firstSection = new Section(firstStation, secondStation, 10, 1L);
        Section secondSection = new Section(secondStation, thirdStation, 10, 1L);
        Section thirdSection = new Section(thirdStation, fourthStation, 10, 1L);
        Section fourthSection = new Section(fourthStation, fifthStation, 10, 1L);

        List<Section> unsortedSections = Arrays.asList(thirdSection, firstSection, fourthSection, secondSection);
        Sections sections = new Sections(unsortedSections);
        assertThat(sections.toList()).containsExactly(firstSection, secondSection, thirdSection, fourthSection);
    }
}
