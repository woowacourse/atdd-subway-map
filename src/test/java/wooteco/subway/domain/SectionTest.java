package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Section 도메인 객체 테스트")
class SectionTest {

    @DisplayName("구간 거리가 1 보다 작을 경우 예외가 발생한다.")
    @Test
    void createSectionUnderDistance1() {
        assertThatThrownBy(() -> new Section(1L, 2L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간 거리는 1 이상이어야 합니다.");
    }
}
