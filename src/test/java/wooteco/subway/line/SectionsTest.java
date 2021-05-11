package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

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
}