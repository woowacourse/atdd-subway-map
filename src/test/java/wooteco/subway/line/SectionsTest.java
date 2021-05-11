package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private static final Station JAMSIL_STATION = new Station(1L, "잠실역");
    private static final Station GANGNAM_STATION = new Station(2L, "강남역");
    private static final Station SAMSUNG_STATION = new Station(3L, "삼성역");

    @Test
    @DisplayName("구간이 주어졌을 때 정렬된 역 id 리스트 생성")
    void sectionSorting() {
        Section first = new Section(JAMSIL_STATION, GANGNAM_STATION, 1);
        Section second = new Section(GANGNAM_STATION, SAMSUNG_STATION, 5);
        Sections sections = new Sections(Arrays.asList(first, second));

        List<Station> actual = sections.path();
        assertThat(actual).containsExactly(JAMSIL_STATION, GANGNAM_STATION, SAMSUNG_STATION);
    }

    @Test
    @DisplayName("상행 부분으로 Section 추가")
    void sectionAddFirst() {
    }
}