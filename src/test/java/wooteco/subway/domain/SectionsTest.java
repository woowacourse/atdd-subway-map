package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.section.NoSuchSectionException;

class SectionsTest {

    @Test
    @DisplayName("정렬된 역 아이디를 반환한다.")
    void ToStationIds_ShuffledSections_SortedStationIdsReturned() {
        // given
        final Line line = new Line(1L, "1", "1");

        final List<Station> stations = generateStations(1, 8);

        final List<Section> shuffledSections = new ArrayList<>(List.of(
                new Section(1L, line, stations.get(0),stations.get(1), 1),
                new Section(2L, line, stations.get(1),stations.get(2), 1),
                new Section(3L, line, stations.get(2),stations.get(3), 1),
                new Section(4L, line, stations.get(3),stations.get(4), 1),
                new Section(5L, line, stations.get(4),stations.get(5), 1),
                new Section(6L, line, stations.get(5),stations.get(6), 1),
                new Section(7L, line, stations.get(6),stations.get(7), 1)
        ));
        Collections.shuffle(shuffledSections);
        final Sections sections = new Sections(shuffledSections);

        // when
        final List<Long> actual = sections.toStation()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        // then
        assertThat(actual).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
    }

    private List<Station> generateStations(final int start, final int end) {
        return IntStream
                .rangeClosed(start, end)
                .mapToObj(it -> new Station((long) it, String.valueOf(it)))
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("상행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_UpEndStationId_SizeOneSectionsReturned() {
        // given
        final Line line = new Line(1L, "1", "1");
        final List<Station> stations = generateStations(1, 4);

        final Sections sections = new Sections(List.of(
                new Section(line, stations.get(0), stations.get(1), 1),
                new Section(line, stations.get(1), stations.get(2), 1),
                new Section(line, stations.get(2), stations.get(3), 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(line, stations.get(0), stations.get(1), 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stations.get(0));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("하행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_DownEndStationId_SizeOneSectionsReturned() {
        // given
        final Line line = new Line(1L, "1", "1");
        final List<Station> stations = generateStations(1, 4);

        final Sections sections = new Sections(List.of(
                new Section(line, stations.get(0), stations.get(1), 1),
                new Section(line, stations.get(1), stations.get(2), 1),
                new Section(line, stations.get(2), stations.get(3), 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(line, stations.get(2), stations.get(3), 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stations.get(3));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("종점이 아닌 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_NotEndStationId_SizeTwoSectionsReturned() {
        // given
        final Line line = new Line(1L, "1", "1");
        final List<Station> stations = generateStations(1, 4);

        final Sections sections = new Sections(List.of(
                new Section(line, stations.get(0), stations.get(1), 1),
                new Section(line, stations.get(1), stations.get(2), 1),
                new Section(line, stations.get(2), stations.get(3), 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(line, stations.get(0), stations.get(1), 1),
                new Section(line, stations.get(1), stations.get(2), 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stations.get(1));

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("구간이 1개인 역은 삭제할 수 없다.")
    void FindDeletableSections_OnlyOneSection_ExceptionThrown() {
        // given
        final Station stationToDelete = new Station(2L, "2");
        final Sections sections = new Sections(List.of(
                new Section(
                        new Line(1L, "1", "1"),
                        new Station(1L, "1"),
                        stationToDelete,
                        1
                )
        ));

        // then
        assertThatThrownBy(() -> sections.findDeletableSections(stationToDelete))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("구간을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 포함된 구간이 존재하지 않으면 예외를 던진다.")
    void FindDeletableSections_DeletableSectionEmpty_ExceptionThrown() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(
                        new Line(1L, "1", "1"),
                        new Station(1L, "1"),
                        new Station(2L, "2"),
                        1
                ),
                new Section(
                        new Line(1L, "1", "1"),
                        new Station(2L, "2"),
                        new Station(3L, "3"),
                        1
                )
        ));

        // then
        final Station station = new Station(999L, "999");
        assertThatThrownBy(() -> sections.findDeletableSections(station))
                .isInstanceOf(NoSuchSectionException.class);
    }
}