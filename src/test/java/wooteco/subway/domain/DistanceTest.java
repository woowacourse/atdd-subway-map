package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DistanceTest {

    @DisplayName("거리가 0이하면 예외처리된다.")
    @Test
    void createDistanceBelowZero() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Distance(0))
            .withMessage("거리 값은 자연수 입니다.");
    }
}
