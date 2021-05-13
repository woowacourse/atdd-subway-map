package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    Set<Section> sectionsToTest;
    private Sections sections;
    private List<Station> stations;
    private Distance distance;

    @BeforeEach
    void SetUp() {
        stations = Arrays.asList(
            new Station(1L, "답십리역"),
            new Station(2L, "왕십리역"),
            new Station(3L, "잠실역"),
            new Station(4L, "강남역"),
            new Station(5L, "홍대입구역")
        );

        distance = new Distance(1);

        sectionsToTest = new HashSet<>(Arrays.asList(
            new Section(1L, stations.get(0), stations.get(1), distance),
            new Section(2L, stations.get(1), stations.get(2), distance),
            new Section(3L, stations.get(2), stations.get(3), distance)
        ));

        sections = new Sections(sectionsToTest);
    }

    @DisplayName("경로를 반환한다.")
    @Test
    void pathByLine() {
        assertThat(sections.path()).isEqualTo(stations.subList(0, 4));
    }

    @DisplayName("첫 번째 역을 반환한다.")
    @Test
    void firstStation() {
        assertThat(sections.firstStation()).isEqualTo(stations.get(0));
    }

    @DisplayName("마지막 역을 반환한다.")
    @Test
    void lastStation() {
        assertThat(sections.lastStation()).isEqualTo(stations.get(3));
    }

    @DisplayName("해당 역이 존재하지 않는지 확인한다.")
    @Test
    void hasStation() {
        assertThat(sections.hasNotStation(stations.get(4))).isTrue();
        assertThat(sections.hasNotStation(stations.get(1))).isFalse();
    }

    @DisplayName("해당 역을 포함하는 모든 구간을 반환한다.")
    @Test
    void sectionWithStation() {
        List<Section> expected1 = Arrays.asList(
            new Section(2L, stations.get(1), stations.get(2), distance),
            new Section(3L, stations.get(2), stations.get(3), distance));

        List<Section> expected2 = Collections.singletonList(
            new Section(3L, stations.get(2), stations.get(3), distance));

        assertThat(sections.sectionsWithStation(stations.get(2))).hasSize(expected1.size());
        assertThat(sections.sectionsWithStation(stations.get(2))).containsAll(expected1);
        assertThat(sections.sectionsWithStation(stations.get(3))).hasSize(expected2.size());
        assertThat(sections.sectionsWithStation(stations.get(3))).containsAll(expected2);
    }

    @DisplayName("현재 구간들의 거리의 합을 구한다.")
    @Test
    void totalDistance() {
        assertThat(sections.totalDistance()).isEqualTo(new Distance(3));
    }
}
