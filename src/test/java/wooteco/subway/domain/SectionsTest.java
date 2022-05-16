package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.section.NoSuchSectionException;

class SectionsTest {

    private Line line;
    private Distance distance;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private Station station7;
    private Station station8;

    private Section section12;
    private Section section23;
    private Section section34;

    @BeforeEach
    void setUpDate() {
        line = new Line(1L, "red-line", "red");
        distance = new Distance(7);

        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
        station5 = new Station(5L, "station5");
        station6 = new Station(6L, "station6");
        station7 = new Station(7L, "station7");
        station8 = new Station(8L, "station8");

        section12 = new Section(1L, line, station1, station2, distance);
        section23 = new Section(1L, line, station2, station3, distance);
        section34 = new Section(1L, line, station3, station4, distance);
    }

    @Test
    @DisplayName("정렬된 역 아이디를 반환한다.")
    void ToStationIds_ShuffledSections_SortedStationIdsReturned() {
        // given
        final List<Section> shuffledSections = new ArrayList<>(List.of(
                section12,
                section23,
                section34,
                new Section(4L, line, station4, station5, distance),
                new Section(5L, line, station5, station6, distance),
                new Section(6L, line, station6, station7, distance),
                new Section(7L, line, station7, station8, distance)
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

    @Test
    @DisplayName("상행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_EndUpStationId_SizeOneSectionsReturned() {
        // given
        final Sections sections = new Sections(List.of(
                section12,
                section23,
                section34
        ));

        final Sections expected = new Sections(List.of(section12));

        // when
        final Sections actual = sections.findDeletableSections(station1);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("하행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_EndDownStationId_SizeOneSectionsReturned() {
        // given
        final Sections sections = new Sections(List.of(
                section12,
                section23,
                section34
        ));

        final Sections expected = new Sections(List.of(section34));

        // when
        final Sections actual = sections.findDeletableSections(station4);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("종점이 아닌 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_NotEndStationId_SizeTwoSectionsReturned() {
        // given
        final Sections sections = new Sections(List.of(
                section12,
                section23,
                section34
        ));

        final Sections expected = new Sections(List.of(
                section12,
                section23
        ));

        // when
        final Sections actual = sections.findDeletableSections(station2);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("구간이 1개인 역은 삭제할 수 없다.")
    void FindDeletableSections_OnlyOneSection_ExceptionThrown() {
        // given
        final Sections sections = new Sections(List.of(section12));

        // then
        assertThatThrownBy(() -> sections.findDeletableSections(station1))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("구간을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 포함된 구간이 존재하지 않으면 예외를 던진다.")
    void FindDeletableSections_DeletableSectionEmpty_ExceptionThrown() {
        // given
        final Sections sections = new Sections(List.of(
                section12,
                section23
        ));

        // then
        assertThatThrownBy(() -> sections.findDeletableSections(station7))
                .isInstanceOf(NoSuchSectionException.class);
    }
}