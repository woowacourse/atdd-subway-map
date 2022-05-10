package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineTest {

    private List<Section> sections = new ArrayList<>();

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
        sections = List.of(section1, section2, section3, section4, section5);
    }

    @Test
    void calculateUpStation() {
        Line line = new Line(1L, "testLine", "color", station1, station6, 10L, sections);
        Station station = line.calculateUpStation();
        assertThat(station).isEqualTo(station1);
    }
}
