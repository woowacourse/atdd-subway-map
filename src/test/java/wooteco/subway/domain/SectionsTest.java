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
        terminalUpStation = new Station("홍대입구역");
        upStation = new Station("신촌역");
        downStation = new Station("이대역");
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
}
