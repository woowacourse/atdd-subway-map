package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private Sections sections;

    private Station terminalUpStation;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        terminalUpStation = new Station(1L, "홍대입구역");
        upStation = new Station(2L, "신촌역");
        downStation = new Station(3L, "이대역");

        sections = new Sections(List.of(
                new Section(1L, terminalUpStation, upStation, 5),
                new Section(1L, upStation, downStation, 5)
        ));
    }

    @DisplayName("구간에 있는 모든 역들을 조회한다.")
    @Test
    void getStations() {
        List<Station> stations = sections.getStations();
        assertAll(
                () -> assertThat(stations.get(0)).isEqualTo(terminalUpStation),
                () -> assertThat(stations.get(1)).isEqualTo(upStation),
                () -> assertThat(stations.get(2)).isEqualTo(downStation)
        );
    }

    @DisplayName("하행 종점의 역을 조회한다.")
    @Test
    void getTerminalUpStation() {
        Station station = sections.getTerminalDownStation();
        assertThat(station).isEqualTo(downStation);
    }

    @DisplayName("상행, 하행 id와 중복되는 구간이 존재하면 true를 반환한다. ")
    @Test
    void checkDuplicateSection() {
        assertThat(sections.isDuplicateSection(1L, 2L)).isTrue();
    }

    @DisplayName("추가하는 구간이 기존 노선의 어느 역과도 일치하지 않으면 true를 반환한다.")
    @Test
    void isNonMatchStations() {
        assertThat(sections.isNonMatchStations(4L, 5L)).isTrue();
    }

    @DisplayName("구간들 중에서 상행역을 같는 구간을 찾는다.")
    @Test
    void getSectionByUpStationId() {
        Section section = sections.getSectionByUpStationId(upStation.getId());
        assertThat(section.getUpStation()).isEqualTo(upStation);
    }

    @DisplayName("구간들 중에서 하행역을 같는 구간을 찾는다.")
    @Test
    void getSectionByDownStationId() {
        Section section = sections.getSectionByDownStationId(downStation.getId());
        assertThat(section.getDownStation()).isEqualTo(downStation);
    }
}
