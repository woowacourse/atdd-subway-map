package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private static final Sections SECTIONS =
            new Sections(List.of(new Section(1L, 1L, 1L, 2L, 10)));
    private static final Section NEW_SECTION =
            new Section(1L, 1L, 3L, 4);

    @DisplayName("구간이 비어있는 경우 Sections 생성 시 예외가 발생한다.")
    @Test
    void createEmptySections() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 존재하지 않습니다.");
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
        Section newSection = new Section(1L, 3L, 4L, 5);

        assertThatThrownBy(() -> SECTIONS.validateSectionInLine(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
    }

    @DisplayName("지하철 구간의 상행역과 하행역이 이미 모두 노선에 포함되어 있는 경우 예외가 발생한다.")
    @Test
    void validateSectionInLineForBothInclude() {
        Section newSection = new Section(1L, 1L, 2L, 5);

        assertThatThrownBy(() -> SECTIONS.validateSectionInLine(newSection))
                .isInstanceOf(IllegalArgumentException.class)
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
        Section newSection = new Section(1L, 1L, 3L, 10);

        assertThatThrownBy(() -> SECTIONS.validateSectionDistance(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간의 길이는 기존 역 사이의 길이보다 작아야합니다.");
    }

    @DisplayName("수정할 구간을 가져온다.")
    @Test
    void getUpdatedSection() {
        Section updatedSection = SECTIONS.getUpdatedSection(NEW_SECTION);

        assertThat(updatedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 3L, 2L, 6));
    }

    @DisplayName("구간 생성시 구간 수정이 필요한 경우 true를 반환한다.")
    @Test
    void isRequireUpdateTrue() {
        assertThat(SECTIONS.isRequireUpdate(NEW_SECTION)).isTrue();
    }

    @DisplayName("구간 생성시 구간 수정이 불필요한 경우 false를 반환한다.")
    @Test
    void isRequireUpdateFalse() {
        Section newSection = new Section(1L, 2L, 3L, 4);

        assertThat(SECTIONS.isRequireUpdate(newSection)).isFalse();
    }
}
