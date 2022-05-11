package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;

    private Section section1;
    private Section section2;
    private Section section3;
    private Section section4;

    private List<Section> sections = new ArrayList<>();
    Sections testSections;

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

        sections = new ArrayList<Section>() {
            {
                add(section1);
                add(section2);
                add(section3);
                add(section4);
            }
        };

        testSections = new Sections(sections);
    }

    @Test
    void containsStation_success() {
        assertThat(testSections.containsStation(station1)).isTrue();
    }

    @Test
    void containsStation_fail() {
        assertThat(testSections.containsStation(station6)).isFalse();
    }

    @Test
    void calculateUpStation() {
        testSections = new Sections(sections);
        Station station = testSections.calculateUpStation();
        assertThat(station).isEqualTo(station1);
    }

    @Test
    void calculateDownStation() {
        testSections = new Sections(sections);
        Station station = testSections.calculateDownStation();
        assertThat(station).isEqualTo(station5);
    }

    @Test
    void remove() {
        testSections.remove(section4);

        assertThat(testSections.containsStation(station5)).isFalse();
    }
}
