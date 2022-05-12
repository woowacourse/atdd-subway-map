package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.exception.section.NoSuchSectionException;

class SectionsTest {

    @Test
    @DisplayName("정렬된 역 아이디를 반환한다.")
    void ToStationIds_ShuffledSections_SortedStationIdsReturned() {
        // given
        final List<Section> shuffledSections = new ArrayList<>(List.of(
                new Section(1L, 1L, 1L, 2L, 1),
                new Section(2L, 1L, 2L, 3L, 1),
                new Section(3L, 1L, 3L, 4L, 1),
                new Section(4L, 1L, 4L, 5L, 1),
                new Section(5L, 1L, 5L, 6L, 1),
                new Section(6L, 1L, 6L, 7L, 1),
                new Section(7L, 1L, 7L, 8L, 1)
        ));
        Collections.shuffle(shuffledSections);
        final Sections sections = new Sections(shuffledSections);

        // when
        final List<Long> actual = sections.toStationIds();

        // then
        assertThat(actual).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
    }

    @Test
    @DisplayName("상행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_UpEndStationId_SizeOneSectionsReturned() {
        // given
        final long stationIdToDelete = 1L;
        final Sections sections = new Sections(List.of(
                new Section(1L, stationIdToDelete, 2L, 1),
                new Section(1L, 2L, 3L, 1),
                new Section(1L, 3L, 4L, 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(1L, stationIdToDelete, 2L, 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stationIdToDelete);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("하행 종점 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_DownEndStationId_SizeOneSectionsReturned() {
        // given
        final long stationIdToDelete = 4L;
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, 2L, 1),
                new Section(1L, 2L, 3L, 1),
                new Section(1L, 3L, stationIdToDelete, 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(1L, 3L, stationIdToDelete, 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stationIdToDelete);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("종점이 아닌 역이 주어졌을 때 삭제 가능한 구간들을 찾는다.")
    void FindDeletableSections_NotEndStationId_SizeTwoSectionsReturned() {
        // given
        final long stationIdToDelete = 2L;
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, stationIdToDelete, 1),
                new Section(1L, stationIdToDelete, 3L, 1),
                new Section(1L, 3L, 4L, 1)
        ));

        final Sections expected = new Sections(List.of(
                new Section(1L, 1L, stationIdToDelete, 1),
                new Section(1L, stationIdToDelete, 3L, 1)
        ));

        // when
        final Sections actual = sections.findDeletableSections(stationIdToDelete);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("구간이 1개인 역은 삭제할 수 없다.")
    void FindDeletableSections_OnlyOneSection_ExceptionThrown() {
        // given
        final long stationIdToDelete = 2L;
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, stationIdToDelete, 1)
        ));

        // then
        assertThatThrownBy(() -> sections.findDeletableSections(stationIdToDelete))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("구간을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 포함된 구간이 존재하지 않으면 예외를 던진다.")
    void FindDeletableSections_DeletableSectionEmpty_ExceptionThrown() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, 2L, 1),
                new Section(1L, 2L, 3L, 1)
        ));

        // then
        assertThatThrownBy(() -> sections.findDeletableSections(999L))
                .isInstanceOf(NoSuchSectionException.class);
    }
}