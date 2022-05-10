package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LineTest {

    @Test
    @DisplayName("노선이 추가되면 자동으로 구간이 추가된다.")
    void createLine() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("청계산입구역");
        //when
        Line line = new Line("신분당선", "빨간색", upStation, downStation, 7);
        //then
        List<Station> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("구간을 추가하면 노선에 포함된 역이 한 개 늘어난다.")
    void addSection() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("청계산입구역");
        Line line = new Line("신분당선", "빨간색", upStation, downStation, 7);
        //when
        Station upStation2 = new Station("청계산입구역");
        Station downStation2 = new Station("정자역");
        Section section = new Section(upStation2, downStation2, 7);
        line.addSection(section);
        //then
        final List<Station> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("구간을 추가할 때 상행 갈래길을 방지한다.")
    void addSectionWithoutBranch() {
        //given
        Station upStation = new Station("강남역");
        Station downStation = new Station("청계산입구역");
        Line line = new Line("신분당선", "빨간색", upStation, downStation, 7);
        //when
        Station upStation2 = new Station("강남역");
        Station downStation2 = new Station("양재역");
        Section section = new Section(upStation2, downStation2, 5);
        line.addSection(section);
        //then
        List<Section> sections = line.getSections();
        final Section section1 = sections.stream()
                .filter(it -> it.getUpStation().equals(upStation))
                .findFirst()
                .orElseThrow();
        final Section section2 = sections.stream()
                .filter(it -> it.getUpStation().equals(downStation2))
                .findFirst()
                .orElseThrow();
        assertAll(
                () -> assertThat(section1.getDownStation()).isEqualTo(downStation2),
                () -> assertThat(section2.getDownStation()).isEqualTo(downStation)
        );
    }
}
