package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DataNotExistException;
import wooteco.subway.exception.SubwayException;

public class SectionsTest {

    private static final Sections SECTIONS =
            new Sections(List.of(
                    new Section(1L, 1L, 1L, 2L, 4),
                    new Section(2L, 1L, 2L, 3L, 6)
            ));
    private static final Section NEW_SECTION =
            new Section(1L, 2L, 4L, 1);

    @DisplayName("Stations 생성시 정렬되어 저장된다.")
    @Test
    void createSortedSections() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 3L, 10),
                new Section(2L, 1L, 4L, 1L, 4),
                new Section(3L, 1L, 3L, 2L, 6)
        ));

        assertThat(sections.findStationIds()).containsExactly(4L, 1L, 3L, 2L);
    }

    @DisplayName("지하철 구간의 상행역과 하행역 중 하나만 노선에 존재하는 경우 예외가 발생하지 않는다.")
    @Test
    void validateSectionInLineNotThrowAnyException() {
        assertThatCode(() -> SECTIONS.validateSectionInLine(NEW_SECTION))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 구간의 상행역과 하행역이 모두 노선에 존재하지 않은 경우 예외가 발생한다.")
    @Test
    void validateSectionInLineForBothExclude() {
        Section newSection = new Section(1L, 4L, 5L, 5);

        assertThatThrownBy(() -> SECTIONS.validateSectionInLine(newSection))
                .isInstanceOf(SubwayException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 구간의 상행역과 하행역이 이미 모두 노선에 포함되어 있는 경우 예외가 발생한다.")
    @Test
    void validateSectionInLineForBothInclude() {
        Section newSection = new Section(1L, 1L, 2L, 5);

        assertThatThrownBy(() -> SECTIONS.validateSectionInLine(newSection))
                .isInstanceOf(SubwayException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
    }

    @DisplayName("지하철 구간의 길이가 기존 역 사이의 길이보다 작은 경우 예외가 발생하지 않는다.")
    @Test
    void validateSectionDistanceNotThrowAnyException() {
        assertThatCode(() -> SECTIONS.validateSectionDistance(NEW_SECTION))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 구간의 길이가 기존 역 사이의 길이보다 크거나 같은 경우 예외가 발생한다.")
    @Test
    void validateSectionDistanceForLongDistance() {
        Section newSection = new Section(1L, 1L, 4L, 4);

        assertThatThrownBy(() -> SECTIONS.validateSectionDistance(newSection))
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간의 길이는 기존 역 사이의 길이보다 작아야합니다.");
    }

    @DisplayName("구간 추가시 수정할 구간을 가져온다.")
    @Test
    void getUpdatedSectionForSave() {
        Section updatedSection = SECTIONS.getUpdatedSectionForSave(NEW_SECTION);

        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(2L, 1L, 4L, 3L, 5));
    }

    @DisplayName("구간 생성시 구간 수정이 필요한 경우 true를 반환한다.")
    @Test
    void isRequireUpdateForSaveTrue() {
        assertThat(SECTIONS.isRequireUpdateForSave(NEW_SECTION)).isTrue();
    }

    @DisplayName("구간 생성시 구간 수정이 불필요한 경우 false를 반환한다.")
    @Test
    void isRequireUpdateForSaveFalse() {
        Section newSection = new Section(1L, 3L, 4L, 4);

        assertThat(SECTIONS.isRequireUpdateForSave(newSection)).isFalse();
    }

    @DisplayName("노선에 등록되지 않은 역을 삭제하는 경우 예외가 발생한다.")
    @Test
    void validateDeleteForNotExistStation() {
        assertThatThrownBy(() -> SECTIONS.validateDelete(4L))
                .isInstanceOf(DataNotExistException.class)
                .hasMessage("해당 노선에 등록되지 않은 역입니다.");
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 삭제하는 경우 예외가 발생한다.")
    @Test
    void validateDeleteForLastSection() {
        Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 4)
        ));

        assertThatThrownBy(() -> sections.validateDelete(2L))
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간이 하나인 노선에서 마지막 구간을 삭제할 수 없습니다.");
    }

    @DisplayName("구간 삭제시 수정할 구간을 가져온다.")
    @Test
    void getUpdatedSectionForDelete() {
        Section updatedSection = SECTIONS.getUpdatedSectionForDelete(2L);

        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, 1L, 3L, 10));
    }

    @DisplayName("구간 삭제시 삭제할 구간을 가져온다.")
    @Test
    void deletedSectionId() {
        Long deletedSectionId = SECTIONS.getDeletedSectionId(2L);

        assertThat(deletedSectionId).isEqualTo(2L);
    }
}
