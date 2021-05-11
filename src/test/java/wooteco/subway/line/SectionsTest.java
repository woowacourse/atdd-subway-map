package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.exception.SectionError;
import wooteco.subway.line.exception.SectionException;
import wooteco.subway.station.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {
    private static final Station JAMSIL_STATION = new Station(1L, "잠실역");
    private static final Station SAMSUNG_STATION = new Station(2L, "삼성역");
    private static final Station GANGNAM_STATION = new Station(3L, "강남역");

    private static final Section JAMSIL_TO_SAMSUNG = new Section(JAMSIL_STATION, SAMSUNG_STATION, 1);
    private static final Section SAMSUNG_TO_GANGNAM = new Section(SAMSUNG_STATION, GANGNAM_STATION, 5);

    private Sections sections;

    @BeforeEach
    void setUp() {
        this.sections = new Sections(Arrays.asList(JAMSIL_TO_SAMSUNG, SAMSUNG_TO_GANGNAM));
    }

    @Test
    @DisplayName("구간이 주어졌을 때 정렬된 역 id 리스트 생성")
    void path() {
        assertThat(sections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("상행 부분으로 Section 추가")
    void sectionAddFirst() {
        Station newStation = new Station(4L, "new");
        Section addFirst = new Section(newStation, JAMSIL_STATION, 3);

        Sections newSections = sections.add(addFirst);

        assertThat(newSections.path()).containsExactly(newStation, JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION);
    }

    @Test
    @DisplayName("하행 부분으로 Section 추가")
    void sectionAddLast() {
        Station newStation = new Station(4L, "new");
        Section addLast = new Section(GANGNAM_STATION, newStation, 3);

        Sections newSections = sections.add(addLast);

        assertThat(newSections.path()).containsExactly(JAMSIL_STATION, SAMSUNG_STATION, GANGNAM_STATION, newStation);
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
}