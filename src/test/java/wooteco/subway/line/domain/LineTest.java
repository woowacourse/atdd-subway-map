package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LineTest {
    private Station station1;
    private Station station2;
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        station1 = new Station(1L, "아마역");
        station2 = new Station(2L, "마찌역");
        section = new Section(1L, station1, station2, 10);
        line = new Line("9호선", "bg-red-600", Arrays.asList(section));
    }

    @Test
    @DisplayName("라인 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Line("신분당선", "bg-red-600"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("초기 구간을 설정한다.")
    void addSection() {
        Line line = new Line("9호선", "bg-red-600");
        Section section = new Section(1L, 1L, station1, station2, 10);

        line.initSections(Arrays.asList(section));
        assertThat(line.stations()).hasSize(2);
    }

    @Test
    @DisplayName("등록하려는 구간으로 등록된 구간들에 이미 존재하는 역을 찾는다.")
    void duplicatedStation() {
        Station station3 = new Station(3L, "잠실역");
        Station findStation = line.registeredStation(new Section(1L, station1, station3, 10));

        assertThat(findStation).isEqualTo(station1);
    }

    @Test
    @DisplayName("해당 station을 upStation으로 가지고 있는 구간을 찾는다.")
    void findSectionWithUpStation() {
        Section findSection = line.findSectionWithUpStation(station1);

        assertThat(findSection).isEqualTo(section);
    }

    @Test
    @DisplayName("해당 station을 downStation으로 가지고 있는 구간을 찾는다.")
    void findSectionWithDownStation() {
        Section findSection = line.findSectionWithDownStation(station2);

        assertThat(findSection).isEqualTo(section);
    }

    @Test
    @DisplayName("하나의 구간만 가지고 있는지 확인한다.")
    void hasOnlyOne() {
        assertThat(line.hasOnlyOneSection()).isTrue();
    }

    @Test
    @DisplayName("해당 역을 가지고 있는 구간을 반환한다.")
    void sectionsWhichHasStation() {
        List<Section> findSections = line.sectionsWhichHasStation(station1);

        assertThat(findSections).containsExactly(section);
    }

    @Test
    @DisplayName("해당 역을 가지고 있는 구간을 반환한다. - 구간을 추가하고 확인")
    void sectionsWhichHasStation2() {
        Station station4 = new Station(4L, "강남역");
        Section newSection = new Section(1L, station2, station4, 5);
        line = new Line("9호선", "bg-red-600", Arrays.asList(section, newSection));

        List<Section> findSections = line.sectionsWhichHasStation(station2);

        assertThat(findSections).containsExactly(section, newSection);
    }
}
