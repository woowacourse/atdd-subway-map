package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConstructionTest {

    private Construction construction;
    private List<Station> stations;
    private Distance distance;

    @BeforeEach
    void SetUp() {
        stations = Arrays.asList(
            new Station(1L, "답십리역"),
            new Station(2L, "왕십리역"),
            new Station(3L, "잠실역"),
            new Station(4L, "강남역"),
            new Station(5L, "홍대입구역"),
            new Station(6L, "신촌역")
        );

        distance = new Distance(5);

        Sections sections = new Sections(new HashSet<>(Arrays.asList(
            new Section(1L, stations.get(0), stations.get(1), distance),
            new Section(2L, stations.get(1), stations.get(2), distance),
            new Section(3L, stations.get(2), stations.get(3), distance),
            new Section(4L, stations.get(3), stations.get(4), distance)
        )));

        Line line = new Line(1L, "1호선", "bg-red-600", sections);

        construction = new Construction(line);
    }

    @DisplayName("구간을 삽입한다.(구간 사이에 삽입한다.)")
    @Test
    void createSectionBetweenSections() {
        // given
        Section section = new Section(stations.get(1), stations.get(5),
            new Distance(1));

        // when
        construction.createSection(section);

        // then
        List<Section> sectionsToCreate = Arrays.asList(
            new Section(stations.get(1), stations.get(5), new Distance(1)),
            new Section(stations.get(5), stations.get(2), new Distance(4))
        );
        List<Section> sectionsToRemove = Collections.singletonList(
            new Section(2L, stations.get(1), stations.get(2), new Distance(5))
        );

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("id")
            .build();

        assertThat(construction.sectionsToCreate())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .usingElementComparatorIgnoringFields().containsAll(sectionsToCreate);
        assertThat(construction.sectionsToRemove()).usingRecursiveFieldByFieldElementComparator()
            .containsAll(sectionsToRemove);
    }

    @DisplayName("구간을 삽입한다.(마지막 구간에 삽입한다.)")
    @Test
    void createSectionAsLastSection() {
        // given
        Section section = new Section(stations.get(5), stations.get(0),
            new Distance(1));

        // when
        construction.createSection(section);

        // then
        List<Section> sectionsToCreate = Collections.singletonList(
            new Section(stations.get(5), stations.get(0), new Distance(1))
        );

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("id")
            .build();

        assertThat(construction.sectionsToCreate())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .usingElementComparatorIgnoringFields().containsAll(sectionsToCreate);
        assertThat(construction.sectionsToRemove()).hasSize(0);
    }

    @DisplayName("구간을 삭제한다. (구간 사이에 있는 역을 삭제한다.)")
    @Test
    void deleteSectionsBetweenSections() {
        // when
        construction.deleteSectionsByStation(stations.get(1));

        // then
        List<Section> sectionsToCreate = Collections.singletonList(
            new Section(stations.get(0), stations.get(2), new Distance(10))
        );
        List<Section> sectionsToRemove = Arrays.asList(
            new Section(1L, stations.get(0), stations.get(1), distance),
            new Section(2L, stations.get(1), stations.get(2), distance)
        );

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("id")
            .build();

        assertThat(construction.sectionsToCreate())
            .usingRecursiveFieldByFieldElementComparator(configuration)
            .usingElementComparatorIgnoringFields().containsAll(sectionsToCreate);
        assertThat(construction.sectionsToRemove()).usingRecursiveFieldByFieldElementComparator()
            .containsAll(sectionsToRemove);
    }

    @DisplayName("구간을 삭제한다. (종점에 있는 역을 삭제한다.)")
    @Test
    void deleteSectionsByLastStation() {
        // when
        construction.deleteSectionsByStation(stations.get(0));

        // then
        List<Section> sectionsToRemove = Collections.singletonList(
            new Section(1L, stations.get(0), stations.get(1), distance)
        );

        assertThat(construction.sectionsToCreate()).hasSize(0);
        assertThat(construction.sectionsToRemove()).usingRecursiveFieldByFieldElementComparator()
            .containsAll(sectionsToRemove);
    }

    @DisplayName("구간이 하나 남은 경우 구간 삭제시 예외 처리한다.")
    @Test
    void deleteSectionsWhenHasOnlyOneSection() {
        // when
        Line line = new Line("2호선", "bg-blue-300",
            new Sections(new Section(stations.get(0), stations.get(1), new Distance(1))));
        Construction constructionWithOnlyOneSection = new Construction(line);

        // then
        assertThatIllegalStateException()
            .isThrownBy(
                () -> constructionWithOnlyOneSection.deleteSectionsByStation(stations.get(0)))
            .withMessage("구간이 하나 남은 경우 삭제할 수 없습니다.");
    }

    @DisplayName("존재하지 않는 역을 삭제할 때 예외 처리한다.")
    @Test
    void deleteSectionWithInvalidStation() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> construction.deleteSectionsByStation(stations.get(5)))
            .withMessage("존재하지 않는 역입니다.");
    }
}
