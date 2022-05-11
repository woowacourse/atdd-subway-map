package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {


    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        //given
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Sections sections = new Sections();
        Section section1TO2 = new Section(1L, station1, station2, 10);

        // when
        sections.add(section1TO2);

        // then
        assertThat(sections.values()).containsExactly(section1TO2);
    }

    @DisplayName("상행 종점으로 구간을 추가한다.")
    @Test
    void addSectionToUp() {
        // given
        Sections sections = new Sections();
        Section section2To3 = new Section(1L, station2, station3, 10);
        Section section1To2 = new Section(2L, station1, station2, 10);

        sections.add(section2To3);

        // when
        sections.add(section1To2);

        // then
        assertThat(sections.values()).containsOnly(section1To2, section2To3);
    }

    @DisplayName("하행 종점으로 구간을 추가한다.")
    @Test
    void addSectionToDown() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(2L, station1, station2, 10);
        Section section2To3 = new Section(1L, station2, station3, 10);

        sections.add(section1To2);

        // when
        sections.add(section2To3);

        // then
        assertThat(sections.values()).containsOnly(section1To2, section2To3);
    }

    @DisplayName("같은 상행, 하행역을 가진 구간이 이미 등록되어 있는 경우 예외를 던진다.")
    @Test
    void throwExceptionWhenAlreadyRegisteredSameStation() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        sections.add(section1To2);

        // when && then
        Section alreadyExist = new Section(2L, station1, station2, 9);
        assertThatThrownBy(() -> sections.add(alreadyExist))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("연결 가능한 구간이 아닙니다.");
    }

    @DisplayName("추가할 구간의 역이 모두 이미 등록되어 있는 경우 예외를 던진다.")
    @Test
    void throwExceptionWhenBothStationsRegistered() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        sections.add(section1To2);
        Section section2To3 = new Section(2L, station2, station3, 10);
        sections.add(section2To3);

        // when && then
        Section section1To3 = new Section(2L, station1, station3, 10);
        assertThatThrownBy(() -> sections.add(section1To3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("연결 가능한 구간이 아닙니다.");
    }

    @DisplayName("연결할 구간이 존재하지 않는 경우 예외를 던진다.")
    @Test
    void cannotAddSection() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        sections.add(section1To2);

        // when && then
        Section section3To4 = new Section(1L, station3, station4, 10);
        assertThatThrownBy(() -> sections.add(section3To4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("연결 가능한 구간이 아닙니다.");
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 상행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseOne() {
        // given
        Sections sections = new Sections();
        Section section1To3 = new Section(1L, station1, station3, 10);
        sections.add(section1To3);

        // when
        Section section1To2 = new Section(2L, station1, station2, 7);
        sections.add(section1To2);
        // then
        Set<Section> values = sections.values();
        assertThat(values).containsOnly(
                new Section(1L, station1, station2, 7),
                new Section(2L, station2, station3, 3)
        );
    }

    @DisplayName("새로운 구간을 등록할 때 갈래길이 생기지 않도록 추가한다 - 하행역이 같은 경우")
    @Test
    void addSectionNotMakingForkRoadCaseTwo() {
        // given
        Sections sections = new Sections();
        Section section1To3 = new Section(1L, station1, station3, 10);
        sections.add(section1To3);

        // when
        Section section2To3 = new Section(2L, station2, station3, 7);
        sections.add(section2To3);

        // then
        Set<Section> values = sections.values();
        assertThat(values).containsOnly(
                new Section(1L, station1, station2, 3),
                new Section(2L, station2, station3, 7)
        );
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 상행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseOne() {
        // given
        Sections sections = new Sections();
        Section section1To3 = new Section(1L, station1, station3, 10);
        sections.add(section1To3);


        // when && then
        Section section1To2 = new Section(2L, station1, station2, 10);
        assertThatThrownBy(() -> sections.add(section1To2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("갈래길이 생기지 않도록 추가할 때 거리 조건을 만족하지 않으면 예외를 던진다 - 하행역이 같은 경우")
    @Test
    void throwExceptionWhenAddSectionNotMakingForkRoadCaseTwo() {
        // given
        Sections sections = new Sections();
        Section section1To3 = new Section(1L, station1, station3, 10);
        sections.add(section1To3);

        // when && then
        Section section2To3 = new Section(2L, station2, station3, 10);
        assertThatThrownBy(() -> sections.add(section2To3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간의 길이가 올바르지 않습니다.");
    }

    @DisplayName("상행 종점을 제거한다.")
    @Test
    void removeUpStation() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        Section section2To3 = new Section(2L, station2, station3, 10);
        sections.add(section1To2);
        sections.add(section2To3);

        // when
        sections.removeStation(station3);

        // then
        assertThat(sections.values()).containsOnly(section1To2);
    }

    @DisplayName("하행 종점을 제거한다.")
    @Test
    void removeDownStation() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        Section section2To3 = new Section(2L, station2, station3, 10);
        Section section3To4 = new Section(3L, station3, station4, 10);
        sections.add(section1To2);
        sections.add(section2To3);
        sections.add(section3To4);

        // when
        sections.removeStation(station4);

        // then
        assertThat(sections.values()).containsOnly(section1To2, section2To3);
    }

    @DisplayName("다른 역 사이에 있는 역을 제거할 경우 구간을 재배치한다.")
    @Test
    void removeInterStation() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        Section section2To3 = new Section(2L, station2, station3, 10);
        Section section3To4 = new Section(3L, station3, station4, 10);
        sections.add(section1To2);
        sections.add(section2To3);
        sections.add(section3To4);

        // when
        sections.removeStation(station2);


        // then
        assertThat(sections.values()).containsOnly(
                new Section(1L, station1, station3, 20),
                new Section(3L, station3, station4, 10)
        );
    }

    @DisplayName("구간이 하나만 존재하는 경우 역을 제거할 수 없다.")
    @Test
    void cannotRemoveStationWhenSingleSection() {
        // given
        Sections sections = new Sections();
        Section section1To2 = new Section(1L, station1, station2, 10);
        sections.add(section1To2);

        // when && then
        assertAll(
                () -> assertThatThrownBy(() -> sections.removeStation(station1))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 경우 역을 제거할 수 없습니다."),
                () -> assertThatThrownBy(() -> sections.removeStation(station2))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("구간이 하나인 경우 역을 제거할 수 없습니다.")
        );
    }

    @DisplayName("입력된 역이 노선 내에 존재하지 않는 경우 예외를 던진다.")
    @Test
    void cannotRemoveNonExistStation() {
        // given
        Sections sections = new Sections();
        sections.add(new Section(1L, station1, station2, 10));
        sections.add(new Section(2L, station2, station3, 10));

        // when && then
        assertThatThrownBy(() -> sections.removeStation(station4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 역이 존재하지 않습니다.");
    }

}
