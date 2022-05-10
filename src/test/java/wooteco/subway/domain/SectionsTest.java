package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("구간 순서대로 역들을 정렬해서 반환한다.")
    @Test
    void getSortedStations() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("역삼역");
        Station station3 = new Station("선릉역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station2, station3, 1);

        Sections sections = new Sections(List.of(section1, section2));

        List<Station> stations = sections.getSortedStations();

        assertThat(stations).containsSequence(station1, station2, station3);
    }

}
