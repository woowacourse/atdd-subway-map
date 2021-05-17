package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NullInputException;
import wooteco.subway.exception.section.InvalidDistanceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    @DisplayName("구간 생성 성공")
    @Test
    void section() {
        Section section = new Section(1L, 2L, 10);
        assertThat(new Section(1L, 2L, 10))
            .usingRecursiveComparison()
            .isEqualTo(section);
    }

    @DisplayName("null 입력 시 예외 발생")
    @Test
    void nullSection() {
        assertThatThrownBy(() -> new Section(null, null, 10))
            .hasMessage(new NullInputException().getMessage());
    }

    @DisplayName("거리에 양의 정수 미입력 시 예외 발생")
    @Test
    void invalidDistance() {
        assertThatThrownBy(() -> new Section(1L, 2L, 0))
            .hasMessage(new InvalidDistanceException().getMessage());

        assertThatThrownBy(() -> new Section(1L, 2L, -1))
            .hasMessage(new InvalidDistanceException().getMessage());
    }
}