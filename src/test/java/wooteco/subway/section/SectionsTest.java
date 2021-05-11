package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionsTest {

    private Sections sections;
    private Long lineId = 3L;

    @BeforeEach
    void setUp() {
        final Station station1 = new Station(1L, "대화역");
        final Station station2 = new Station(2L, "주엽역");
        final Station station3 = new Station(3L, "정발산역");
        final Station station4 = new Station(4L, "마두역");
        final Station station5 = new Station(5L, "백석역");

        final Section section1 = new Section(lineId, station1, station2, 2);
        final Section section2 = new Section(lineId, station2, station3, 2);
        final Section section3 = new Section(lineId, station3, station4, 2);
        final Section section4 = new Section(lineId, station4, station5, 2);

        sections = new Sections(lineId, Arrays.asList(section1, section2, section3, section4));
    }

    @DisplayName("LineId가 다른 Section이 포함된다면 예외가 발생한다")
    @Test
    void createValidation() {
        final Long lineId = 3L;
        final Long differentLineId = 5L;

        final Station station1 = new Station(1L, "대화역");
        final Station station2 = new Station(2L, "주엽역");
        final Station station3 = new Station(3L, "정발산역");

        final Section section1 = new Section(lineId, station1, station2, 2);
        final Section section2 = new Section(differentLineId, station2, station3, 2);

        assertThatThrownBy(() -> new Sections(lineId, Arrays.asList(section1, section2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("노선에 없는 구간이 포함되어 있습니다.");
    }

    @DisplayName("Station의 목록을 상행~하행 순서로 반환한다")
    @Test
    void lineUpStations() {
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"),
                new Station(5L, "백석역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("노선의 상행종점 위로 구간을 추가할 수 있다")
    @Test
    void insertSectionUpperUpEndStation() {
        //given
        final Station station = new Station(6L, "대화보다위에역");
        final Section section = new Section(lineId, station, new Station(1L, "대화역"), 2);
        //when
        sections.insertSection(section);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(6L, "대화보다위에역"),
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"),
                new Station(5L, "백석역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("노선의 하행종점 아래로 구간을 추가할 수 있다")
    @Test
    void insertSectionUnderDownEndStation() {
        //given
        final Station station = new Station(6L, "백석보다아래역");
        final Section section = new Section(lineId, new Station(5L, "백석역"), station, 2);
        //when
        sections.insertSection(section);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"),
                new Station(5L, "백석역"),
                new Station(6L, "백석보다아래역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("노선의 역들 사이로 구간을 추가할 수 있다")
    @Test
    void insertSectionInBetweenStations() {
        //given
        final Station station = new Station(6L, "마두백석사이역");
        final Section section = new Section(lineId, station, new Station(5L, "백석역"), 1);
        //when
        sections.insertSection(section);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"),
                new Station(6L, "마두백석사이역"),
                new Station(5L, "백석역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("노선의 역들 사이로 구간을 추가할 때, 기존 구간보다 더 큰 거리를 추가하려하면 예외처리한다.")
    @Test
    void insertSectionExceptionWhenSectionOverDistanceLimit() {
        //given
        final Station station = new Station(6L, "마두백석사이역");
        final Section section = new Section(lineId, station, new Station(5L, "백석역"), 5);
        //when & then
        assertThatThrownBy(() -> sections.insertSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로운 구간의 거리는 기존 구간의 거리보다 작아야 합니다");
    }

    @DisplayName("구간 추가를 요청한 노선에, 구간에서 요청한 upStationId, downStationId가 둘 다 없다면 예외처리 한다")
    @Test
    void insertSectionExceptionWhenBothStationsNotInLine() {
        //given
        final Station station1 = new Station(10L, "없는역");
        final Station station2 = new Station(11L, "생뚱맞은역");
        final Section section = new Section(lineId, station1, station2, 5);
        //when & then
        assertThatThrownBy(() -> sections.insertSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결될 수 있는 구간이 아닙니다.");
    }

    @DisplayName("구간 추가를 요청한 노선에, 이미 upStationId와 downStationId가 존재한다면 예외 처리를 한다")
    @Test
    void insertSectionExceptionWhenBothStationsAlreadyInLine() {
        //given
        final Station station1 = new Station(1L, "대화역");
        final Station station3 = new Station(3L, "정발산역");
        final Section section = new Section(lineId, station1, station3, 5);
        //when & then
        assertThatThrownBy(() -> sections.insertSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결될 수 있는 구간이 아닙니다.");
    }

    @DisplayName("중간에 끼어있는 Station을 구간에서 지운다")
    @Test
    void removeSectionInBetween() {
        //given
        final Station station3 = new Station(3L, "정발산역");
        //when
        sections.removeSection(station3);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(4L, "마두역"),
                new Station(5L, "백석역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("맨 위 Station을 구간에서 지운다")
    @Test
    void removeSectionTop() {
        //given
        final Station station1 = new Station(1L, "대화역");
        //when
        sections.removeSection(station1);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"),
                new Station(5L, "백석역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("맨 아래 Station을 구간에서 지운다")
    @Test
    void removeSectionBottom() {
        //given
        final Station station5 = new Station(5L, "백석역");
        //when
        sections.removeSection(station5);
        //then
        final List<Station> lineUpStations = sections.lineUpStations();
        final List<Station> expectedOrder = Arrays.asList(
                new Station(1L, "대화역"),
                new Station(2L, "주엽역"),
                new Station(3L, "정발산역"),
                new Station(4L, "마두역"));
        assertThat(lineUpStations.equals(expectedOrder)).isTrue();
    }

    @DisplayName("구간에 속하지 않은 Station에 대한 구간 삭제는 에러를 뱉는다")
    @Test
    void removeSectionExceptionNoStation() {
        assertThatThrownBy(() -> sections.removeSection(new Station(6L, "없는역")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에 속하지 않은 역입니다.");
    }
    
    @DisplayName("구간이 하나인 노선에서는 구간제거를 할 수 없다")
    @Test
    void removeSectionExceptionOneSectionLeft() {
        //given
        final Station station1 = new Station(1L, "대화역");
        final Station station2 = new Station(2L, "주엽역");
        final Station station3 = new Station(3L, "정발산역");
        final Station station4 = new Station(4L, "마두역");
        final Station station5 = new Station(5L, "백석역");
        //when
        sections.removeSection(station1);
        sections.removeSection(station2);
        sections.removeSection(station3);
        //then
        assertThatThrownBy(() -> sections.removeSection(station4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 하나밖에 없어 삭제할 수 없습니다");
    }

    @DisplayName("양끝 구간을 검증해낸다")
    @Test
    void checkSectionEdge() {
        final boolean atEdge = sections.checkSectionAtEdge(new Station(1L, "대화역"));
        assertThat(atEdge).isTrue();
    }
}
