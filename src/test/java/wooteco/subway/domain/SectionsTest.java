package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.SectionsToBeCreatedAndUpdated;
import wooteco.subway.dto.SectionsToBeDeletedAndUpdated;
import wooteco.subway.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class SectionsTest {

    private final Long lineId = 1L;
    private final Long lastUpStationId = 3L;
    private final Long middleStationId = 1L;
    private final Long lastDownStationId = 2L;
    private final Long newStationId = 4L;

    private Section section1;
    private Section section2;
    private Sections sections;

    @BeforeEach
    void setUp() {
        section1 = new Section(1L, lineId, lastUpStationId, middleStationId, 10);
        section2 = new Section(2L, lineId, middleStationId, lastDownStationId, 10);
        sections = new Sections(List.of(section1, section2));
    }

    @DisplayName("상행 구간을 등록한다.")
    @Test
    void addLastUpSection() {
        Section newSection = new Section(lineId, newStationId, lastUpStationId, 10);
        SectionsToBeCreatedAndUpdated result = sections.add(newSection);

        assertAll(
                () -> assertThat(result.getSectionToBeCreated()).isEqualTo(newSection),
                () -> assertThat(result.getSectionToBeUpdated()).isNull()
        );
    }

    @DisplayName("하행 구간을 등록한다.")
    @Test
    void addLastDownSection() {
        Section newSection = new Section(lineId, lastDownStationId, newStationId, 10);
        SectionsToBeCreatedAndUpdated result = sections.add(newSection);

        assertAll(
                () -> assertThat(result.getSectionToBeCreated()).isEqualTo(newSection),
                () -> assertThat(result.getSectionToBeUpdated()).isNull()
        );
    }

    @DisplayName("중간 구간을 등록한다. (기존 구간의 앞쪽에 등록)")
    @Test
    void addMiddleSectionInFrontOfExistSection() {
        Section newSection = new Section(lineId, lastUpStationId, newStationId, 7);
        SectionsToBeCreatedAndUpdated result = sections.add(newSection);

        Section updatedSection = new Section(section1.getId(), section1.getLineId(),
                newStationId, section1.getDownStationId(), section1.getDistance() - 7);
        assertAll(
                () -> assertThat(result.getSectionToBeCreated()).isEqualTo(newSection),
                () -> assertThat(result.getSectionToBeUpdated()).isEqualTo(updatedSection)
        );
    }

    @DisplayName("중간 구간을 등록한다. (기존 구간의 뒤쪽에 등록)")
    @Test
    void addMiddleSectionBehindOfExistSection() {
        Section newSection = new Section(lineId, newStationId, middleStationId, 7);
        SectionsToBeCreatedAndUpdated result = sections.add(newSection);

        Section updatedSection = new Section(section1.getId(), section1.getLineId(),
                section1.getUpStationId(), newStationId, section1.getDistance() - 7);
        assertAll(
                () -> assertThat(result.getSectionToBeCreated()).isEqualTo(newSection),
                () -> assertThat(result.getSectionToBeUpdated()).isEqualTo(updatedSection)
        );
    }

    @DisplayName("새로운 구간의 길이가 기존 구간의 길이보다 같거나 큰 경우 예외가 발생한다.")
    @Test
    void throwsExceptionWhenNewSectionDistanceLongerThanExistSection() {
        Section newSection = new Section(lineId, newStationId, middleStationId, 10);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("새로운 구간의 길이는 기존 역 사이의 길이보다 작아야 합니다.");
    }

    @DisplayName("생성하려는 구간의 역이 모두 노선에 존재하지 않으면 생성할시 예외가 발생한다.")
    @Test
    void throwsExceptionWhenAddSectionWithNotExistStations() {
        Section newSection = new Section(lineId, 100L, 101L, 10);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("구간을 추가하기 위해서는 노선에 들어있는 역이 필요합니다.");
    }

    @DisplayName("생성하려는 구간의 역이 모두 노선에 존재하지 않으면 생성할시 예외가 발생한다.")
    @Test
    void throwsExceptionWhenAddSectionWithAllExistStationInLine() {
        Section newSection = new Section(lineId, lastUpStationId, lastDownStationId, 10);

        assertThatThrownBy(() -> sections.add(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @DisplayName("상행역을 삭제한다.")
    @Test
    void deleteLastUpStation() {
        SectionsToBeDeletedAndUpdated result = sections.delete(lastUpStationId);

        assertAll(
                () -> assertThat(result.getSectionToBeRemoved()).isEqualTo(section1),
                () -> assertThat(result.getSectionToBeUpdated()).isNull()
        );
    }

    @DisplayName("하행역을 삭제한다.")
    @Test
    void deleteLastDownStation() {
        SectionsToBeDeletedAndUpdated result = sections.delete(lastDownStationId);

        assertAll(
                () -> assertThat(result.getSectionToBeRemoved()).isEqualTo(section2),
                () -> assertThat(result.getSectionToBeUpdated()).isNull()
        );
    }

    @DisplayName("중간역을 삭제한다.")
    @Test
    void deleteMiddleStation() {
        SectionsToBeDeletedAndUpdated result = sections.delete(middleStationId);

        Section updatedSection = new Section(section1.getId(), section1.getLineId(),
                section1.getUpStationId(), section2.getDownStationId(), section1.getDistance() + section2.getDistance());
        assertAll(
                () -> assertThat(result.getSectionToBeRemoved()).isEqualTo(section2),
                () -> assertThat(result.getSectionToBeUpdated()).isEqualTo(updatedSection)
        );
    }

    @DisplayName("노선에 존재하지 않는 역을 삭제할 시 예외가 발생한다.")
    @Test
    void throwsExceptionWhenDeleteStationNotExistInLine() {
        assertThatThrownBy(() -> sections.delete(newStationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageMatching("현재 라인에 존재하지 않는 역입니다.");
    }

    @DisplayName("구간이 하나인 노선은 구간 삭제가 불가능하다.")
    @Test
    void throwsExceptionWhenDeleteStationWithOnlyOneSectionInLine() {
        Sections sectionsThatHaveOneSection = new Sections(List.of(section1));
        assertThatThrownBy(() -> sectionsThatHaveOneSection.delete(middleStationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageMatching("구간이 하나인 노선에서는 구간 삭제가 불가합니다.");
    }

    @DisplayName("역의 id를 상행부터 하행 순으로 반환한다.")
    @Test
    void getSortedStationIds() {
        List<Long> sortedStationIds = sections.getSortedStationIds();
        List<Long> expected = List.of(3L, 1L, 2L);

        assertThat(sortedStationIds).isEqualTo(expected);
    }
}
