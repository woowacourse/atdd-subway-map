package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {
    @DisplayName("추가하는 구간의 하행 종점이 노선의 상행 종점이면 첫 구간으로 추가된다")
    @Test
    void add_first() {
        Station upTermination = new Station(1L, "상행종점역");
        Station downTermination = new Station(2L, "하행종점역");
        Section section = new Section(upTermination, downTermination, 10);
        Sections sections = new Sections(List.of(section));

        Station station = new Station(3L, "새로운역");
        Section newSection = new Section(station, upTermination, 5);
        sections.add(newSection);

        assertThat(sections.getAllStations().get(0)).isEqualTo(station);
    }

    @DisplayName("추가하는 구간의 상행 종점이 노선의 하행 종점이면 마지막 구간으로 추가된다")
    @Test
    void add_last() {
        Station upTermination = new Station(1L, "상행종점역");
        Station downTermination = new Station(2L, "하행종점역");
        Section section = new Section(upTermination, downTermination, 10);
        Sections sections = new Sections(List.of(section));

        Station station = new Station(3L, "새로운역");
        Section newSection = new Section(downTermination, station, 5);
        sections.add(newSection);

        List<Station> allStations = sections.getAllStations();
        assertThat(allStations.get(allStations.size() - 1)).isEqualTo(station);
    }
}
