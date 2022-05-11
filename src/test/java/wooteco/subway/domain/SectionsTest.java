package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @DisplayName("section 리스트에서 station id를 추출한다")
    @Test
    void extractStationIds() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 3);
        Section section2 = Section.of(line, station2, station3, 3);
        Section section3 = Section.of(line, station3, station4, 3);
        Sections sections = new Sections(List.of(section1, section2, section3));
        List<Station> stations = sections.extractStations();

        assertThat(stations.size()).isEqualTo(4);
        assertThat(stations.contains(station1)).isTrue();
        assertThat(stations.contains(station2)).isTrue();
        assertThat(stations.contains(station3)).isTrue();
        assertThat(stations.contains(station4)).isTrue();
    }

    @DisplayName("객체가 생성될 때 section이 정렬된다.")
    @Test
    void initTest() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Station station5 = Station.of("5");
        Station station6 = Station.of("6");
        Station station7 = Station.of("7");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station2, station3, 5);
        Section section2 = Section.of(line, station6, station7, 5);
        Section section3 = Section.of(line, station4, station5, 5);
        Section section4 = Section.of(line, station1, station2, 5);
        Section section5 = Section.of(line, station3, station4, 5);
        Section section6 = Section.of(line, station5, station6, 5);

        Sections sections = new Sections(List.of(section1, section2, section3, section4, section5, section6));
        List<Section> ordered = sections.getSections();

        assertThat(ordered.get(5).getUpStation()).isEqualTo(station1);
        assertThat(ordered.get(4).getUpStation()).isEqualTo(station2);
        assertThat(ordered.get(3).getUpStation()).isEqualTo(station3);
        assertThat(ordered.get(2).getUpStation()).isEqualTo(station4);
        assertThat(ordered.get(1).getUpStation()).isEqualTo(station5);
        assertThat(ordered.get(0).getUpStation()).isEqualTo(station6);
    }

    @DisplayName("upStation, downStation 모두 존재하는 sections에 포함돼 있으면 예외가 발생한다.")
    @Test
    void addFail_alreadyContainedStations() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station2, station3, 5);
        Section section2 = Section.of(line, station1, station2, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(line, station1, station3, 3);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
    }

    @DisplayName("upStation, downStation 모두 sections에 없으면 예외가 발생한다..")
    @Test
    void addFail_notContainedStations() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Station station5 = Station.of("5");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station2, station3, 5);
        Section section2 = Section.of(line, station1, station2, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(line, station4, station5, 3);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 중 하나는 기존 section에 포함되어 있어야 합니다.");
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_upstream")
    @Test
    void addFail_largeDistance() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station5 = Station.of("5");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 5);
        Section section2 = Section.of(line, station2, station3, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(line, station1, station5, 7);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 section의 역 간 거리는 존재하는 section의 역 간 거리보다 작아야 합니다.");
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다_downstream")
    @Test
    void addFail_largeDistance2() {
        Station station0 = Station.of("0");
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 5);
        Section section2 = Section.of(line, station2, station3, 5);

        Sections sections = new Sections(List.of(section1, section2));
        Section section = Section.of(line, station0, station2, 7);

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가하려는 section이 이미 존재하는 section보다 distance가 크면 예외가 발생한다")
    @Test
    void addFail_largeDistance3() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station3, 5);
        Sections sections = new Sections(List.of(section1));
        Section section2 = Section.of(line, station1, station2, 8);
        assertThatThrownBy(() -> sections.add(section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상행 종점 연장")
    @Test
    void addUpStation() {
        Station station0 = Station.of("0");
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 10);
        Sections sections = new Sections(List.of(section1));

        Section section = Section.of(line, station0, station1, 5);
        SectionBuffer sectionBuffer = sections.add(section);

        assertThat(sectionBuffer.getAddBuffer().size()).isEqualTo(1);
        assertThat(sectionBuffer.getDeleteBuffer().size()).isEqualTo(0);
    }

    @DisplayName("하행 종점 연장")
    @Test
    void addDownStation() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 10);
        Sections sections = new Sections(List.of(section1));

        Section section = Section.of(line, station2, station3, 5);
        SectionBuffer sectionBuffer = sections.add(section);

        assertThat(sectionBuffer.getAddBuffer().size()).isEqualTo(1);
        assertThat(sectionBuffer.getDeleteBuffer().size()).isEqualTo(0);
    }

    @DisplayName("노선에 section이 1개이면 section삭제 시 예외가 발생한다.")
    @Test
    void deleteFail() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Line line = Line.of("2호선", "초록색");
        Section section1 = Section.of(line, station1, station2, 10);
        Sections sections = new Sections(List.of(section1));

        assertThatThrownBy(() -> sections.delete(station1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 역을 삭제할 수 없습니다. 노선에 역은 최소 2개는 존재해야 합니다.");
    }

    @DisplayName("노선에서 양 끝 종점이 아닌 section을 삭제하면 기존 section이 연결된다.")
    @Test
    void deleteSuccess_NoTerminalSection() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Line line = Line.of("2호선", "초록색");
        Section section1 = Section.of(line, station1, station2, 10);
        Section section2 = Section.of(line, station2, station3, 10);
        Section section3 = Section.of(line, station3, station4, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        SectionBuffer sectionBuffer = sections.delete(station2);

        assertThat(sectionBuffer.getAddBuffer().size()).isEqualTo(1);
        assertThat(sectionBuffer.getAddBuffer().get(0).getUpStation()).isEqualTo(station1);
        assertThat(sectionBuffer.getAddBuffer().get(0).getDownStation()).isEqualTo(station3);
        assertThat(sectionBuffer.getAddBuffer().get(0).getDistance()).isEqualTo(20);
        assertThat(sectionBuffer.getDeleteBuffer().size()).isEqualTo(2);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getUpStation()).isEqualTo(station2);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDownStation()).isEqualTo(station3);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDistance()).isEqualTo(10);
        assertThat(sectionBuffer.getDeleteBuffer().get(1).getUpStation()).isEqualTo(station1);
        assertThat(sectionBuffer.getDeleteBuffer().get(1).getDownStation()).isEqualTo(station2);
        assertThat(sectionBuffer.getDeleteBuffer().get(1).getDistance()).isEqualTo(10);
    }

    @DisplayName("노선에서 상행 종점 section이 삭제한다.")
    @Test
    void deleteSuccess_UpTerminalSection() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 10);
        Section section2 = Section.of(line, station2, station3, 10);
        Section section3 = Section.of(line, station3, station4, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        SectionBuffer sectionBuffer = sections.delete(station1);

        assertThat(sectionBuffer.getAddBuffer().size()).isEqualTo(0);
        assertThat(sectionBuffer.getDeleteBuffer().size()).isEqualTo(1);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getUpStation()).isEqualTo(station1);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDownStation()).isEqualTo(station2);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDistance()).isEqualTo(10);
    }

    @DisplayName("노선에서 하행 종점 section이 삭제한다.")
    @Test
    void deleteSuccess_DownTerminalSection() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Station station3 = Station.of("3");
        Station station4 = Station.of("4");
        Line line = Line.of("2호선", "초록색");

        Section section1 = Section.of(line, station1, station2, 10);
        Section section2 = Section.of(line, station2, station3, 10);
        Section section3 = Section.of(line, station3, station4, 10);
        Sections sections = new Sections(List.of(section1, section2, section3));

        SectionBuffer sectionBuffer = sections.delete(station4);

        assertThat(sectionBuffer.getAddBuffer().size()).isEqualTo(0);
        assertThat(sectionBuffer.getDeleteBuffer().size()).isEqualTo(1);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getUpStation()).isEqualTo(station3);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDownStation()).isEqualTo(station4);
        assertThat(sectionBuffer.getDeleteBuffer().get(0).getDistance()).isEqualTo(10);
    }
}
