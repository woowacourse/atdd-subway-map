package wooteco.subway.section.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.EmptyInputException;
import wooteco.subway.exception.NullInputException;
import wooteco.subway.exception.section.InvalidAddSectionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setup() {
        this.sections = new Sections(Arrays.asList(
            new Section(1L, 2L, 2),
            new Section(2L, 3L, 3)
        ));
    }

    @DisplayName("null 입력시 예외 발생")
    @Test
    void nullSections() {
        assertThatThrownBy(() -> new Sections(null))
            .hasMessage(new NullInputException().getMessage());
    }

    @DisplayName("빈 요소 입력시 예외 발생")
    @Test
    void emptySections() {
        assertThatThrownBy(() -> new Sections(Collections.emptyList()))
            .hasMessage(new EmptyInputException().getMessage());
    }

    @DisplayName("종점 구간 추가 검증 성공")
    @Test
    void addEndPointSection() {
        Section section = new Section(3L, 4L, 5);
        assertDoesNotThrow(() -> sections.validate(section));
    }

    @DisplayName("중간역 구간 추가 검증 성공")
    @Test
    void addIntermediateSection() {
        Section section = new Section(2L, 4L, 2);
        assertDoesNotThrow(() -> sections.validate(section));
    }

    @DisplayName("미연결 구간 추가 시 예외 발생")
    @Test
    void addDisconnectedSection() {
        Section section = new Section(5L, 6L, 2);
        assertThatThrownBy(() -> sections.validate(section))
            .hasMessage(new InvalidAddSectionException().getMessage());
    }

    @DisplayName("이미 존재하는 구간 추가 시 예외 발생")
    @Test
    void addExistingSection() {
        Section section = new Section(1L, 2L, 2);
        assertThatThrownBy(() -> sections.validate(section))
            .hasMessage(new InvalidAddSectionException().getMessage());
    }

    @DisplayName("유효하지 않은 거리 구간 추가 시 예외 발생")
    @Test
    void addInvalidDistanceSection() {
        Section section = new Section(2L, 3L, 4);
        assertThatThrownBy(() -> sections.validate(section))
            .hasMessage(new InvalidAddSectionException().getMessage());
    }

    @DisplayName("구간 정렬 성공")
    @Test
    void sortStationIdsInLine() {
        Sections sections = new Sections(Arrays.asList(
            new Section(4L, 5L, 1),
            new Section(1L, 2L, 2),
            new Section(3L, 4L, 4),
            new Section(2L, 3L, 3)
        ));

        List<Long> sortedStationIds = sections.sortedStationIds();

        assertThat(sortedStationIds)
            .isEqualTo(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }

    @DisplayName("역 삭제 시 구간 합침 성공")
    @Test
    void mergeSection() {
        Section mergedSection = sections.merge(2L);
        Section expectedSection = new Section(1L, 3L, 5);

        assertThat(mergedSection).isEqualTo(expectedSection);
    }
}
