package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ValidationFailureException;

class SectionTest {

    private static final Section SECTION =
        Section.Builder().id(1L).lineId(1L).upStationId(1L).downStationId(2L).distance(10).build();


    @DisplayName("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 예외가 발생한다.")
    @Test
    void validateSmaller() {
        assertThatThrownBy(() -> SECTION.validateSmaller(10))
            .isInstanceOf(ValidationFailureException.class)
            .hasMessage("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
    }

    @DisplayName("중간에 역을 추가하면서 바꿔야할 구간을 가져온다.")
    @Test
    void createUpdatedSection() {
        Section updatedSection = SECTION.createUpdatedSection(1L, 3L, 3);
        assertThat(updatedSection).isEqualTo(
            Section.Builder().id(1L).lineId(1L).upStationId(3L).downStationId(2L).distance(7).build()
        );

        updatedSection = SECTION.createUpdatedSection(3L, 2L, 3);
        assertThat(updatedSection).isEqualTo(
            Section.Builder().id(1L).lineId(1L).upStationId(1L).downStationId(3L).distance(7).build()
        );
    }
}