package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SubwayException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        boolean isSame = sections.isUpStation(1L);
        boolean isNotSame = sections.isUpStation(2L);

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
        boolean isSame = sections.isDownStation(4L);
        boolean isNotSame = sections.isDownStation(3L);

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

    @DisplayName("들어온 Section의 각 역이 종점인지 검증 - 두 역이 모두 종점인 경우")
    @Test
    void twoEndPoints() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));
        Section section = new Section(1L, 1L, 1L, 4L, 3);

        // when & then
        assertThatThrownBy(() -> sections.validatesEndPoints(section))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("들어온 Section의 각 역이 종점인지 검증 - 두 역이 모두 종점이 아닌 경우")
    @Test
    void zeroEndPoints() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));
        Section section = new Section(1L, 1L, 2L, 3L, 3);

        // when & then
        assertThatThrownBy(() -> sections.validatesEndPoints(section))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("upStationId로 일치하는 section 찾기")
    @Test
    void findByUpStationId() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        Section section = sections.findByUpStationId(2L);

        // then
        assertThat(section).usingRecursiveComparison()
                .isEqualTo(new Section(2L, 1L, 2L, 3L, 4));
    }

    @DisplayName("없는 upStationId로 section을 찾는 경우")
    @Test
    void findByNotExistUpStationId() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when & then
        assertThatThrownBy(() -> sections.findByUpStationId(5L))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("downStationId로 일치하는 section 찾기")
    @Test
    void findByDownStationId() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when
        Section section = sections.findByDownStationId(2L);

        // then
        assertThat(section).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, 1L, 2L, 3));
    }

    @DisplayName("없는 downStationId로 section을 찾는 경우")
    @Test
    void findByNotExistDownStationId() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));

        // when & then
        assertThatThrownBy(() -> sections.findByUpStationId(5L))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("추가하려는 section의 downStationId가 상행종점과 같은지 확인")
    @Test
    void isUpEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));
        Section endPointSection = new Section(4L, 1L, 5L, 1L, 3);
        Section noEndPointSection = new Section(4L, 1L, 5L, 3L, 3);

        // when
        boolean isSame = sections.isEndPoint(endPointSection);
        boolean isNotSame = sections.isEndPoint(noEndPointSection);

        // then
        assertTrue(isSame);
        assertFalse(isNotSame);
    }

    @DisplayName("추가하려는 section의 upStationId가 하행종점과 같은지 확인")
    @Test
    void isDownEndPoint() {
        // given
        Sections sections = new Sections(Arrays.asList(
                new Section(1L, 1L, 1L, 2L, 3),
                new Section(2L, 1L, 2L, 3L, 4),
                new Section(3L, 1L, 3L, 4L, 5)
        ));
        Section endPointSection = new Section(4L, 1L, 4L, 5L, 3);
        Section noEndPointSection = new Section(4L, 1L, 3L, 3L, 3);

        // when
        boolean isSame = sections.isEndPoint(endPointSection);
        boolean isNotSame = sections.isEndPoint(noEndPointSection);

        // then
        assertTrue(isSame);
        assertFalse(isNotSame);
    }

    @DisplayName("노선에 남아있는 구간 개수가 1이하인 경우 예외발생")
    @Test
    void checkRemainSectionSize() {
        // given
        Sections sections = new Sections(Collections.singletonList(
                new Section(1L, 1L, 1L, 2L, 3)
        ));

        // when & then
        assertThatThrownBy(sections::checkRemainSectionSize)
                .isInstanceOf(SubwayException.class);
    }
}