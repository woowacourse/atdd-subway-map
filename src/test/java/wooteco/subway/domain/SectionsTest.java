package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {
    private final Station upTermination = new Station(1L, "상행종점역");
    private final Station downTermination = new Station(2L, "하행종점역");
    private Sections sections;

    @BeforeEach
    void setUp() {
        Section section = new Section(upTermination, downTermination, 10);
        sections = new Sections(List.of(section));
    }

    @DisplayName("추가하는 구간의 하행 종점이 노선의 상행 종점이면 첫 구간으로 추가된다")
    @Test
    void add_first() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(station, upTermination, 5);
        sections.add(section);

        assertThat(sections.getAllStations().get(0)).isEqualTo(station);
    }

    @DisplayName("추가하는 구간의 상행 종점이 노선의 하행 종점이면 마지막 구간으로 추가된다")
    @Test
    void add_last() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(downTermination, station, 5);
        sections.add(section);

        List<Station> allStations = sections.getAllStations();
        assertThat(allStations.get(allStations.size() - 1)).isEqualTo(station);
    }

    @DisplayName("추가하는 구간의 양 방향 종점이 모두 노선에 존재하지 않으면 예외가 발생한다.")
    @Test
    void add_no_station() {
        Station station1 = new Station(3L, "새로운역");
        Station station2 = new Station(4L, "또다른역");
        Section section = new Section(station1, station2, 3);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.add(section))
                .withMessageContaining("존재하지 않아");
    }

    @DisplayName("기존 구간과 하행 종점이 같은 구간이 추가될 경우 기존 구간이 분리되고 그 뒤에 새 구간이 추가된다.")
    @Test
    void add_split_left() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(station, downTermination, 3);
        sections.add(section);
        LinkedList<Section> sectionValues = this.sections.getSections();

        assertAll(
                () -> assertThat(sectionValues.get(0).getDistance()).isEqualTo(7),
                () -> assertThat(sectionValues.get(1)).isEqualTo(section)
        );
    }

    @DisplayName("기존 구간과 상행 종점이 같은 구간이 추가될 경우 기존 구간이 분리되고 그 앞에 새 구간이 추가된다.")
    @Test
    void add_split_right() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(upTermination, station, 3);
        sections.add(section);
        LinkedList<Section> sectionValues = sections.getSections();

        assertAll(
                () -> assertThat(sectionValues.get(1).getDistance()).isEqualTo(7),
                () -> assertThat(sectionValues.get(0)).isEqualTo(section)
        );
    }

    @DisplayName("노선의 상행 종점을 삭제할 경우 다음 역이 종점이 된다.")
    @Test
    void delete_upTermination() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(upTermination, station, 3);
        sections.add(section);
        sections.delete(upTermination);

        List<Station> allStations = sections.getAllStations();
        assertThat(allStations).containsExactly(station, downTermination);
    }

    @DisplayName("노선의 하행 종점을 삭제할 경우 이전 역이 종점이 된다.")
    @Test
    void delete_downTermination() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(station, downTermination, 3);
        sections.add(section);
        sections.delete(downTermination);

        List<Station> allStations = sections.getAllStations();
        assertThat(allStations).containsExactly(upTermination, station);
    }

    @DisplayName("노선의 중간역을 삭제할 경우 구간이 합쳐진다.")
    @Test
    void delete_middle() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(station, downTermination, 3);
        sections.add(section);
        sections.delete(station);

        LinkedList<Section> resultSections = sections.getSections();
        List<Station> allStations = sections.getAllStations();

        assertAll(
                () -> assertThat(resultSections).hasSize(1),
                () -> assertThat(resultSections.get(0).getDistance()).isEqualTo(10),
                () -> assertThat(allStations).containsExactly(upTermination, downTermination)
        );
    }

    @DisplayName("노선에 구간이 하나 뿐일 때 삭제하면 예외가 발생한다.")
    @Test
    void delete_only_one_section() {
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> sections.delete(downTermination))
                .withMessageContaining("하나 뿐");
    }

    @DisplayName("노선에 없는 역을 삭제하면 예외가 발생한다.")
    @Test
    void delete_no_such_station() {
        Station station = new Station(3L, "새로운역");
        Section section = new Section(station, downTermination, 3);
        sections.add(section);

        Station otherStation = new Station(4L, "또다른역");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> sections.delete(otherStation))
                .withMessageContaining("존재하지 않습니다");
    }
}
