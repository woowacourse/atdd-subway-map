package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Sections sections;
    private List<Station> stations;
    private List<Line> lines;
    private Distance distance;

    @BeforeEach
    void SetUp() {
        lines = Arrays.asList(
            new Line(1L, "1호선", "bg-red-600"),
            new Line(2L, "2호선", "bg-blue-600")
        );

        stations = Arrays.asList(
            new Station(1L, "답십리역"),
            new Station(2L, "왕십리역"),
            new Station(3L, "잠실역"),
            new Station(4L, "강남역"),
            new Station(5L, "홍대입구역")
        );

        distance = new Distance(1);

        Set<Section> sectionsToTest = new HashSet<>(Arrays.asList(
            new Section(1L, lines.get(0), stations.get(0), stations.get(1), distance),
            new Section(2L, lines.get(0), stations.get(1), stations.get(2), distance),
            new Section(3L, lines.get(0), stations.get(2), stations.get(3), distance),
            new Section(4L, lines.get(1), stations.get(1), stations.get(4), distance)
        ));

        sections = new Sections(sectionsToTest);
    }

    @DisplayName("선택한 노선의 경로를 반환한다.")
    @Test
    void pathByLine() {
        List<Station> expected1 = stations.subList(0, 4);
        List<Station> expected2 = Arrays.asList(
            stations.get(1),
            stations.get(4)
        );

        assertThat(sections.pathByLine(lines.get(0))).isEqualTo(expected1);
        assertThat(sections.pathByLine(lines.get(1))).isEqualTo(expected2);
    }

    @DisplayName("해당 노선의 구간들을 가져온다.")
    @Test
    void sectionsByLine() {
        Set<Section> expected = new HashSet<>(Arrays.asList(
            new Section(1L, lines.get(0), stations.get(0), stations.get(1), distance),
            new Section(2L, lines.get(0), stations.get(1), stations.get(2), distance),
            new Section(3L, lines.get(0), stations.get(2), stations.get(3), distance)
        ));

        assertThat(sections.sectionsByLine(lines.get(0))).hasSize(expected.size());
        assertThat(sections.sectionsByLine(lines.get(0))).containsAll(expected);
    }

    @DisplayName("현재 구간들의 거리의 합을 구한다.")
    @Test
    void totalDistance() {
        assertThat(sections.totalDistance()).isEqualTo(new Distance(4));
    }
}
