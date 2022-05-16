package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private static final long LINE_ID = 1L;
    private Sections sections;
    private Station upStation;
    private Station middleStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        upStation = new Station(1L, "홍대입구역");
        middleStation = new Station(2L, "신촌역");
        downStation = new Station(3L, "이대역");

        sections = new Sections(List.of(
                new Section(LINE_ID, upStation, middleStation, 5),
                new Section(LINE_ID, middleStation, downStation, 5)
        ));
    }

    @DisplayName("구간에 있는 모든 역들을 조회한다.")
    @Test
    void getStations() {
        List<Station> stations = sections.getStations();
        assertThat(stations).hasSize(3)
                .containsExactly(
                        upStation, middleStation, downStation
                );
    }

    @DisplayName("구간에 있는 모든 역들을 상행에서 하행순서대로 조회한다.")
    @Test
    void getOrderStations() {
        Station newStation = new Station(4L, "하행종점역");
        Sections sections = new Sections(List.of(
                new Section(LINE_ID, downStation, newStation, 4),
                new Section(LINE_ID, upStation, middleStation, 5),
                new Section(LINE_ID, middleStation, downStation, 5)
        ));
        List<Station> stations = sections.getStations();

        assertThat(stations).hasSize(4)
                .containsExactly(
                        upStation,
                        middleStation,
                        downStation,
                        newStation
                );
    }

    @DisplayName("상행, 하행 id와 중복되는 구간이 존재하면 true를 반환한다.")
    @Test
    void checkDuplicateSection() {
        assertThat(sections.isDuplicateSection(1L, 2L)).isTrue();
    }

    @DisplayName("추가하는 구간이 기존 노선의 어느 역과도 일치하지 않으면 true를 반환한다.")
    @Test
    void isNonMatchStations() {
        assertThat(sections.isNonMatchStations(4L, 5L)).isTrue();
    }

    @DisplayName("종점이 아닌 구간 중 해당하는 구간을 찾는다.")
    @Test
    void findTargetWithNotTerminal() {
        Section section = sections.findTargetWithNotTerminal(upStation, middleStation).get();
        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(upStation),
                () -> assertThat(section.getDownStation()).isEqualTo(middleStation)
        );
    }

    @DisplayName("상행 종점역이 포함된 구간을 제거하면 남은 구간들중 제거한 역은 존재하지 않는다.")
    @Test
    void deleteUpStationSection() {
        Sections deleteSection = sections.delete(LINE_ID, upStation);
        assertThat(deleteSection.getStations().contains(upStation)).isFalse();
    }

    @DisplayName("하행 종점역이 포함된 구간을 제거하면 남은 구간들중 제거한 역은 존재하지 않는다.")
    @Test
    void deleteDownStationSection() {
        Sections deleteSection = sections.delete(LINE_ID, downStation);
        assertThat(deleteSection.getStations().contains(downStation)).isFalse();
    }

    @DisplayName("사이에 구간을 제거하면 2개의 구간이 제거되고 하나의 새로운 구간이 만들어진다.")
    @Test
    void delete() {
        int 전체_구간_개수 = 2;
        Sections deleteSection = sections.delete(LINE_ID, middleStation);
        assertThat(deleteSection.getStations().contains(middleStation)).isFalse();
        assertThat(deleteSection.getSections().size()).isEqualTo(전체_구간_개수 - 1);
    }

}
