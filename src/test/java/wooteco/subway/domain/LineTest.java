package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private List<Section> initialSections;

    Station station1;
    Station station2;
    Station station3;
    Station station4;
    Station station5;
    Station station6;

    Section section1;
    Section section2;
    Section section3;
    Section section4;
    Section section5;
    Sections sections;

    @BeforeEach
    public void setUp() {
        station1 = new Station(1L, "testStation1");
        station2 = new Station(2L, "testStation2");
        station3 = new Station(3L, "testStation3");
        station4 = new Station(4L, "testStation4");
        station5 = new Station(5L, "testStation5");
        station6 = new Station(6L, "testStation6");

        section1 = new Section(1L, 1L, station1, station2, 10L);
        section2 = new Section(2L, 1L, station2, station3, 10L);
        section3 = new Section(3L, 1L, station3, station4, 10L);
        section4 = new Section(4L, 1L, station4, station5, 10L);
        section5 = new Section(5L, 1L, station5, station6, 10L);
        initialSections = new ArrayList<Section>() {
            {
                add(section1);
                add(section2);
                add(section3);
            }
        };
        sections = new Sections(initialSections);
    }

    @DisplayName("새로운 상행 종점 추가")
    @Test
    void add_upEndStation() {
        Line line = new Line(1L, "testLine", "color", station1, station4, 10L, sections);
        Section newUpSection = new Section(1L, station5, station1, 20L);

        line.addSection(newUpSection);

        Station upStation = line.getSections().calculateUpStation();
        assertThat(upStation).isEqualTo(station5);
    }

    @DisplayName("새로운 하행 종점 추가")
    @Test
    void add_downEndStation() {
        Line line = new Line(1L, "testLine", "color", station1, station4, 10L, sections);
        Section newDownSection = new Section(1L, station4, station5, 20L);

        line.addSection(newDownSection);

        Station downStation = line.getSections().calculateDownStation();
        assertThat(downStation).isEqualTo(station5);
    }

    @DisplayName("기존에 있던 역을 새로운 구간 상행역과 겹치게 추가")
    @Test
    public void add_betweenUpperStation() {
        Line line = new Line(1L, "testLine", "color", station1, station4, 10L, sections);
        Section newSection = new Section(1L, station2, station5, 2L);

        line.addSection(newSection);

        assertThat(line.getSections().containsStation(station5)).isTrue();
    }

    @DisplayName("기존에 있던 역을 새로운 구간 하행역과 겹치게 추가")
    @Test
    public void add_betweenLowerStation() {
        Line line = new Line(1L, "testLine", "color", station1, station4, 10L, sections);
        Section newSection = new Section(1L, station5, station2, 2L);

        line.addSection(newSection);

        assertThat(line.getSections().containsStation(station5)).isTrue();
    }

    @DisplayName("추가하려는 구간의 길이가 기존 존재하는 구간의 길이보다 기렴ㄴ 예외 반환")
    @Test
    public void add_longerSection() {
        Line line = new Line(1L, "testLine", "color", station1, station4, 10L, sections);
        Section newSection = new Section(1L, station5, station2, 11L);

        assertThatThrownBy(() -> line.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
