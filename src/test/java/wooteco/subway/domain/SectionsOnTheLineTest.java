package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalSectionException;

class SectionsOnTheLineTest {

    private Station station_GN = new Station(1L, "강남역");
    private Station station_YS = new Station(2L, "역삼역");
    private Station station_SL = new Station(3L, "선릉역");
    private Station station_HD = new Station(4L, "홍대역");
    private Station station_SS = new Station(5L, "상수역");
    private Section section_GN_YS = new Section(1L, station_GN, station_YS, 10);
    private Section section_YS_SL = new Section(2L, station_YS, station_SL, 20);

    private SectionsOnTheLine sectionsOnTheLine;

    @BeforeEach
    void setUp() {
        sectionsOnTheLine = new SectionsOnTheLine(List.of(section_GN_YS, section_YS_SL));
    }

    @DisplayName("추가하고자 하는 구간의 상하역 모두가 노선에 존재하면 예외를 발생시킨다.")
    @Test
    void isAddableOnTheLine_doAllStationExist() {
        Section section = new Section(3L, station_GN, station_SL, 5);

        assertThatThrownBy(() -> sectionsOnTheLine.isAddableOnTheLine(section))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("[ERROR] 부적절한 구간입니다.");
    }

    @DisplayName("추가하고자 하는 구간의 상하역 모두가 노선에 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void isAddableOnTheLine_doNotAllStationExist() {
        Section section = new Section(3L, station_HD, station_SS, 5);

        assertThatThrownBy(() -> sectionsOnTheLine.isAddableOnTheLine(section))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("[ERROR] 부적절한 구간입니다.");
    }

    @DisplayName("추가하고자 하는 구간이 길이가 겹쳐지는 구간길이보다 크거나 같으면 예외를 발생시킨다.")
    @Test
    void isAddableOnTheLine_validateSectionDistance() {
        Section section = new Section(3L, station_GN, station_SS, 11);

        assertThatThrownBy(() -> sectionsOnTheLine.isAddableOnTheLine(section))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("[ERROR] 부적절한 구간입니다.");
    }

    @DisplayName("추가하고자 하는 구간이 노선 중간에 추가 가능한지 판별한다.")
    @Test
    void isAddableOnTheLine() {
        Section section = new Section(3L, station_GN, station_SS, 5);

        assertThat(sectionsOnTheLine.isAddableOnTheLine(section)).isTrue();
    }

    @DisplayName("추가하고자 하는 구간과 겹쳐지는 노선위의 구간을 찾는다.")
    @Test
    void findOverlapSection() {
        Section section = new Section(3L, station_GN, station_SS, 3);

        Section overlapSection = sectionsOnTheLine.findOverlapSection(section);

        assertAll(
                () -> assertThat(overlapSection.getUpStation().getName()).isEqualTo("강남역"),
                () -> assertThat(overlapSection.getDownStation().getName()).isEqualTo("역삼역")
        );
    }

    @DisplayName("구간들을 상행종점부터 하행 종점까지 정렬한다.")
    @Test
    void lineUpStations() {
        List<Station> stations = sectionsOnTheLine.lineUpStations();

        assertAll(
                () -> assertThat(stations.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(stations.get(1).getName()).isEqualTo("역삼역"),
                () -> assertThat(stations.get(2).getName()).isEqualTo("선릉역")
        );
    }

    @DisplayName("해당역이 상행, 하행 종점인지 판별한다.")
    @Test
    void isTerminus() {
        assertThat(sectionsOnTheLine.isTerminus(station_GN)).isTrue();
    }

    @DisplayName("해당역이 노선 위에 존재하는지 판별한다.")
    @Test
    void contains() {
        assertThat(sectionsOnTheLine.contains(station_YS)).isTrue();
    }

    @DisplayName("상행역이 일치하는 구간을 찾는다.")
    @Test
    void findByUpStation() {
        Section section = sectionsOnTheLine.findByUpStation(station_YS);

        assertThat(section.getDownStation().getName()).isEqualTo("선릉역");
    }

    @DisplayName("행역이 일치하는 구간을 찾는다.")
    @Test
    void findByDownStation() {
        Section section = sectionsOnTheLine.findByDownStation(station_YS);

        assertThat(section.getUpStation().getName()).isEqualTo("강남역");
    }

    @DisplayName("노선에 구간이 단 하나만 존재하는지 판별한다.")
    @Test
    void hasSingleSection() {
        assertThat(new SectionsOnTheLine(List.of(section_GN_YS)).hasSingleSection()).isTrue();
    }
}
