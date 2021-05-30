package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.exception.SectionError;
import wooteco.subway.section.exception.SectionException;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private static final Station JAMSIL_STATION = new Station(1L, "잠실역");
    private static final Station SAMSUNG_STATION = new Station(2L, "삼성역");
    private static final Station GANGNAM_STATION = new Station(3L, "강남역");

    private static final int DISTANCE_JAMSIL_SAMSUNG = 10;
    private static final int DISTANCE_SAMSUNG_GANGNAM = 6;
    private static final Section JAMSIL_TO_SAMSUNG = new Section(JAMSIL_STATION, SAMSUNG_STATION, DISTANCE_JAMSIL_SAMSUNG);
    private static final Section SAMSUNG_TO_GANGNAM = new Section(SAMSUNG_STATION, GANGNAM_STATION, DISTANCE_SAMSUNG_GANGNAM);

    private Sections sections;

    @BeforeEach
    void setUp() {
        this.sections = new Sections(Arrays.asList(JAMSIL_TO_SAMSUNG, SAMSUNG_TO_GANGNAM));
    }

    @Test
    @DisplayName("구간이 주어졌을 때 정렬된 역 리스트 생성")
    void path() {
        assertThat(sections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("상행 부분으로 Section 추가")
    void sectionAddFirst() {
        Station newStation = new Station(4L, "new");
        Section addFirst = new Section(newStation, JAMSIL_STATION, 3);

        sections.add(addFirst);

        assertThat(sections.path()).containsExactly(newStation, JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("하행 부분으로 Section 추가")
    void sectionAddLast() {
        Station newStation = new Station(4L, "new");
        Section addLast = new Section(GANGNAM_STATION, newStation, 3);

        sections.add(addLast);

        assertThat(sections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION, newStation);
    }

    @Test
    @DisplayName("두 역 모두 노선에 포함되어 있는 경우 에러 발생")
    void sectionAddBothInPath() {
        Section section = new Section(GANGNAM_STATION, SAMSUNG_STATION, 3);


        assertThatThrownBy(() -> sections.add(section)).isInstanceOf(SectionException.class)
                                                       .hasMessage(SectionError.BOTH_STATION_IN_PATH.getMessage());
    }

    @Test
    @DisplayName("두 역 모두 노선에 포함되지 않은 경우 에러 발생")
    void sectionAddNoneInPath() {
        Station stationA = new Station(4L, "new");
        Station stationB = new Station(5L, "new2");
        Section section = new Section(stationA, stationB, 3);

        assertThatThrownBy(() -> sections.add(section)).isInstanceOf(SectionException.class)
                                                       .hasMessage(SectionError.NONE_STATION_IN_PATH.getMessage());
    }

    @Test
    @DisplayName("하행으로부터 갈림길 방지 연결")
    void addFromDownStation() {
        Station station = new Station(5L, "new");
        Section section = new Section(station, SAMSUNG_STATION, 3);

        sections.add(section);

        assertThat(sections.path()).containsExactly(JAMSIL_STATION, station, SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("상행으로부터 갈림길 방지 연결")
    void addFromUpStation() {
        Station station = new Station(5L, "new");
        Section section = new Section(SAMSUNG_STATION, station, 3);

        sections.add(section);

        assertThat(sections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION, station, GANGNAM_STATION);
    }

    @Test
    @DisplayName("상행역 삭제")
    void deleteUpStation() {
        sections.delete(JAMSIL_STATION);

        assertThat(sections.path()).containsExactly(SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("하행역 삭제")
    void deleteDownStation() {
        sections.delete(GANGNAM_STATION);

        assertThat(sections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION);
    }

    @Test
    @DisplayName("중간역 삭제")
    void deleteMiddleStation() {
        sections.delete(SAMSUNG_STATION);
        List<Section> deletedSections = sections.getSections();
        Section section = deletedSections.get(0);

        assertThat(section.getDistance()).isEqualTo(DISTANCE_JAMSIL_SAMSUNG + DISTANCE_SAMSUNG_GANGNAM);

        assertThat(sections.path()).containsExactly(JAMSIL_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("1개 구간 이하일 때 삭제시 에러")
    void deleteStationOverMinSize() {
        sections.delete(SAMSUNG_STATION);

        assertThatThrownBy(() -> sections.delete(GANGNAM_STATION))
                .isInstanceOf(SectionException.class)
                .hasMessage(SectionError.CANNOT_DELETE_SECTION_SIZE_LESS_THAN_TWO.getMessage());
    }
}
