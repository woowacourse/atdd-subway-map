package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

class SectionTest {

    @DisplayName("주어진 구간이 종점 구간인지 확인한다.")
    @Test
    public void endPoint() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("수서역");
        Station station3 = new Station("일원역");
        Station station4 = new Station("도곡역");
        Station station5 = new Station("개포역");
        Station station6 = new Station("잠실역");
        Line line = new Line("2호선", "bg-blue-500");

        Section section1 = new Section(1L, line, station1, station2, 1);
        Section section2 = new Section(1L, line, station2, station4, 2);
        Section section3 = new Section(1L, line, station4, station5, 1);
        Sections sections = new Sections(Arrays.asList(section1, section2, section3));

        Section endPointSection = new Section(1L, line, station5, station6, 3);
        Section middlePointSection = new Section(1L, line, station3, station4, 1);

        assertThat(endPointSection.isEndPointOf(sections)).isTrue();
        assertThat(middlePointSection.isEndPointOf(sections)).isFalse();
    }
}
