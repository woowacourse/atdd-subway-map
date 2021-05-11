package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 구간 일급 컬렉션 테스트")
class SectionsTest {

    @DisplayName("sorting 테스트")
    @Test
    void sorting() {
        // given
        List<Section> sectionList = Arrays.asList(
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(3L, 1L, 3L, 4L, 5)
        );

        // when
        Sections sections = new Sections(sectionList);

        // then
        assertThat(sections.getSections()).usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(2L, 1L, 2L, 3L, 4),
                        new Section(3L, 1L, 3L, 4L, 5)
                ));
    }

    @DisplayName("현재 노선의 상행 종점이 추가하려는 구간의 끝점과 같은지 확인")
    @Test
    void isUpEndStationEqualsSectionDownStation() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        boolean isSame = sections.isUpEndStationEqualsSectionDownStation(
                new Section(4L, 1L, 5L, 1L, 1));
        boolean isNotSame = sections.isUpEndStationEqualsSectionDownStation(
                new Section(4L, 1L, 5L, 2L, 1));

        // then
        assertTrue(isSame);
        assertFalse(isNotSame);
    }

    @DisplayName("현재 노선의 하행 종점이 추가하려는 구간의 시작점과 같은지 확인")
    @Test
    void isDownEndStationEqualsSectionUpStation() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        boolean isSame = sections.isDownEndStationEqualsSectionUpStation(
                new Section(4L, 1L, 4L, 5L, 1));
        boolean isNotSame = sections.isDownEndStationEqualsSectionUpStation(
                new Section(4L, 1L, 3L, 5L, 1));

        // then
        assertTrue(isSame);
        assertFalse(isNotSame);
    }

    @DisplayName("추가하려는 구간의 시작점이 노선에 등록된 구간들의 upStation에 존재하는 경우")
    @Test
    void sectionUpStationInStartPoints() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        boolean isExist = sections.sectionUpStationInStartPoints(
                new Section(4L, 1L, 2L, 5L, 1));
        boolean isNotExist = sections.sectionUpStationInStartPoints(
                new Section(4L, 1L, 4L, 3L, 1));

        // then
        assertTrue(isExist);
        assertFalse(isNotExist);
    }

    @DisplayName("추가하려는 구간의 끝점이 노선에 등록된 구간들의 downStation에 존재하는 경우")
    @Test
    void sectionDownStationInEndPoints() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        boolean isExist = sections.sectionDownStationInEndPoints(
                new Section(4L, 1L, 4L, 3L, 1));
        boolean isNotExist = sections.sectionDownStationInEndPoints(
                new Section(4L, 1L, 3L, 5L, 1));

        // then
        assertTrue(isExist);
        assertFalse(isNotExist);
    }
}